/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Igor Zinken - http://www.igorski.nl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
#include "audioparticleevent.h"
#include "audioengine.h"
#include <events/drumevent.h>
#include <math.h>
#include <limits>

/* constructor / destructor */

AudioParticleEvent::AudioParticleEvent( float aFrequency, int aPosition,
                                        float aLength, SynthInstrument* aInstrument )
{
    init( aInstrument, aFrequency, aPosition, aLength, true, false );

    _adsr->setAttack ( aInstrument->adsr->getAttack()  );
    _adsr->setRelease( aInstrument->adsr->getRelease() );
}

AudioParticleEvent::AudioParticleEvent( float aFrequency, SynthInstrument* aInstrument,
                                        float aLength, float aAttack, float aRelease )
{
    init( aInstrument, aFrequency, 0, aLength, false, false );

    length = ( aLength == 0 ) ? AudioEngine::bytes_per_beat : aLength;

    // very Kosm
    _minLength    = AudioEngine::bytes_per_bar / 4;
    _sampleLength = AudioEngine::bytes_per_bar;
    _hasMinLength = false;                        // keep track if the min length has been rendered
    
    if ( aAttack != 0 )
        _adsr->setAttack( aAttack ); // otherwise clone of SynthInstrument

    _adsr->setRelease( aRelease );
}
 
AudioParticleEvent::~AudioParticleEvent()
{

}

/* public methods */

void AudioParticleEvent::setFrequency( float aFrequency, bool allOscillators, bool storeAsBaseFrequency )
{
    SynthEvent::setFrequency( aFrequency, allOscillators, storeAsBaseFrequency );

    if ( _type == PercussionTypes::KICK_808 )
        initKarplusStrong();
}

void AudioParticleEvent::calculateBuffers()
{
    SynthEvent::calculateBuffers();

    if ( _type == PercussionTypes::KICK_808 )
        initKarplusStrong();
}

void AudioParticleEvent::reset()
{
    _hasMinLength      = false;
    _queuedForDeletion = false;
    _deleteMe          = false;

    calculateBuffers();

    // re-add to Sequencer if this event has been removed

    for ( int i; i < _instrument->liveAudioEvents->size(); i++ )
    {
        if ( _instrument->liveAudioEvents->at( i ) == this )
            return;
    }
    addToSequencer();
}

void AudioParticleEvent::destroy()
{
    _cancel = true;
    BaseAudioEvent::destroy();
}

/* protected methods */

void AudioParticleEvent::render( AudioBuffer* aOutputBuffer )
{
    _rendering = true;

    int bufferLength = aOutputBuffer->bufferSize;

    SAMPLE_TYPE amp = 0.0;
    SAMPLE_TYPE tmp, am, dpw;

    // following waveforms require alternate volume multipliers
    SAMPLE_TYPE sawAmp = !isSequenced ? .7  : 1.0;
    SAMPLE_TYPE swAmp  = !isSequenced ? .25 : .005;

    // Kosm specific hackaroni
    if ( _type == PercussionTypes::KICK_808 )
    {
        // where t60 is the decay time of our note. It is reasonable to say that this decay time is
        // the time length of the note we want to synthesize. It is referred to as the t60 because
        // the term e-bt/2 in Eq. 16 will decay by -60 dB when t = t60.
        // f_note = kick frequency 50 Hz is 808 kick
        SAMPLE_TYPE t60 = ( SAMPLE_TYPE ) length, v = 287.7, f_note = 50.0;
        SAMPLE_TYPE k, a;

        SAMPLE_TYPE tau = 1.0f / ( SAMPLE_TYPE )( AudioEngineProps::SAMPLE_RATE );
        SAMPLE_TYPE w   = TWO_PI * f_note;
        SAMPLE_TYPE b   = -2.0 * log( 0.001 ) / t60;

        for ( int i = 0; i < bufferLength; ++i )
        {
            k    = w * w + b * b / 4.0f;
            amp += v * tau;
            a    = -k * amp - b * v;
            v   += a * tau;

            if ( _cancel ) return;

            // add a percussive, clicking sound to make it audible on phone speakers
            _ringBuffer->enqueue(( StickDecayFactor * (( _ringBuffer->dequeue() + _ringBuffer->peek()) / 2 )));
            SAMPLE_TYPE value = ( amp + ( _ringBuffer->peek() * .15 )) * _volume;

            for ( int c = 0, ca = aOutputBuffer->amountOfChannels; c < ca; ++c )
                aOutputBuffer->getBufferForChannel( c )[ i ] = value;
        }
        _rendering = false;
    }
    else if ( _type == PercussionTypes::SNARE )
    {
        // --- snare-type sound

        int rnd                   = AudioEngineProps::SAMPLE_RATE;
        int ksbufSize             = 200;
        int* ksbuf                = new int[ ksbufSize ];
        SAMPLE_TYPE ampMultiplier = .5 / ( std::numeric_limits<short>::max() * .5 ); // or perhaps just .5d and that's it ??

        for ( int i = 0; i < ksbufSize; ++i )
        {
            ksbuf[ i ] = 0x4000 + (( rnd >> 16 ) & 0x3fff );
            rnd       *= 1103515245;
        }

        for ( int i = 0, off = 0; i < bufferLength; ++i )
        {
            int nextOff = ( off + 1 ) % ksbufSize;
            int y = ( ksbuf[ off ] + ksbuf[ nextOff ]) / 2;

            if (( rnd & 0x10000 ) == 0 )
                y = -y;

            rnd *= 1103515245;

            ksbuf[ off ] = y;
            off          = nextOff;

            amp = ( y * ampMultiplier ) * .5;

            SAMPLE_TYPE value = amp * _volume;

            if ( _cancel ) return;

            for ( int c = 0, ca = aOutputBuffer->amountOfChannels; c < ca; ++c )
                aOutputBuffer->getBufferForChannel( c )[ i ] = value;
        }
        delete ksbuf;   // release memory
        _rendering = false;
    }
    else {
        SynthEvent::render( aOutputBuffer );
    }
}
