/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014 Igor Zinken - http://www.igorski.nl
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
#include "basesynthevent.h"
#include "../audioengine.h"
#include "../sequencer.h"
#include "../global.h"
#include "../utilities/utils.h"
#include <cmath>

/* constructor / destructor */

BaseSynthEvent::BaseSynthEvent()
{

}

/**
 * initializes an BaseSynthEvent for use in a sequenced context
 *
 * @param aFrequency   {int}    the frequency of the note to synthesize in Hz
 * @param aPosition    {int}    the step position in the sequencer
 * @param aLength      {float} the length in steps the note lasts
 * @param aInstrument  {SynthInstrument} the instruments properties
 * @param aAutoCache   {bool} whether to start caching automatically, this is
 *                     only available if AudioEngineProps::EVENT_CACHING is true
 */
BaseSynthEvent::BaseSynthEvent( float aFrequency, int aPosition, float aLength,
                                SynthInstrument *aInstrument, bool aAutoCache )
{
    init( aInstrument, aFrequency, aPosition, aLength, true );
    setAutoCache( aAutoCache );
}

/**
 * initializes an BaseSynthEvent to be synthesized at once, for a live instrument context
 *
 * @param aFrequency  {int} the frequency of the note to synthesize in Hz
 * @param aInstrument {SynthInstrument}
 */
BaseSynthEvent::BaseSynthEvent( float aFrequency, SynthInstrument *aInstrument )
{
    init( aInstrument, aFrequency, 0, 1, false );
}

BaseSynthEvent::~BaseSynthEvent()
{
    removeFromSequencer();

    if ( _adsr != 0 )
    {
        delete _adsr;
        _adsr = 0;
    }
}

/* public methods */

/**
 * will only occur for a sequenced BaseSynthEvent
 */
void BaseSynthEvent::mixBuffer( AudioBuffer* outputBuffer, int bufferPos,
                                int minBufferPosition, int maxBufferPosition,
                                bool loopStarted, int loopOffset, bool useChannelRange )
{
    // is EVENT_CACHING is enabled, read from cached buffer

    if ( AudioEngineProps::EVENT_CACHING )
    {
        BaseAudioEvent::mixBuffer( outputBuffer, bufferPos, minBufferPosition, maxBufferPosition,
                                   loopStarted, loopOffset, useChannelRange );
    }
    else
    {
        // EVENT_CACHING is disabled, synthesize on the fly

        lock();

        // over the max position ? read from the start ( implies that sequence has started loop )
        if ( bufferPos >= maxBufferPosition )
        {
            if ( useChannelRange )
                bufferPos -= maxBufferPosition;

            else if ( !loopStarted )
                bufferPos -= ( maxBufferPosition - minBufferPosition );
        }

        int bufferEndPos = bufferPos + AudioEngineProps::BUFFER_SIZE;

        if (( bufferPos >= _sampleStart || bufferEndPos > _sampleStart ) &&
              bufferPos < _sampleEnd )
        {
            // render the snippet
            _cacheWriteIndex = _sampleStart > bufferPos ? 0 : bufferPos - _sampleStart;
            int writeOffset  = _sampleStart > bufferPos ? _sampleStart - bufferPos : 0;

            render( _buffer ); // overwrites old buffer contents
            outputBuffer->mergeBuffers( _buffer, 0, writeOffset, MAX_PHASE );

            // reset of properties at end of write
            if ( _cacheWriteIndex >= _sampleLength )
                calculateBuffers();
        }

        if ( loopStarted && bufferPos >= loopOffset )
        {
            bufferPos = minBufferPosition + loopOffset;

            if ( bufferPos >= _sampleStart && bufferPos <= _sampleEnd )
            {
                _cacheWriteIndex = 0; // render the snippet from the start
                render( _buffer );    // overwrites old buffer contents
                outputBuffer->mergeBuffers( _buffer, 0, loopOffset, MAX_PHASE );

                // reset of properties at end of write
                if ( _cacheWriteIndex >= _sampleLength )
                    calculateBuffers();
            }
        }
        unlock();
    }
}

AudioBuffer* BaseSynthEvent::getBuffer()
{
    if ( AudioEngineProps::EVENT_CACHING )
    {
        // if caching hasn't completed, fill cache fragment
        if ( !_cachingCompleted )
            doCache();
    }
    return _buffer;
}

float BaseSynthEvent::getFrequency()
{
    return _frequency;
}

void BaseSynthEvent::setFrequency( float aFrequency )
{
    _frequency = aFrequency;
}

/**
 * @param aPosition position in the sequencer where this event starts playing
 * @param aLength length (in sequencer steps) of this event
 * @param aInstrument the SynthInstrument whose properties will be used for synthesizing this event
 *                    can be NULL to keep the current SynthInstrument, if not null the current
 *                    SynthInstrument is replaced
 */
void BaseSynthEvent::invalidateProperties( int aPosition, float aLength, SynthInstrument *aInstrument )
{
    // swap instrument if new one is different to existing reference
    if ( aInstrument != 0 && _instrument != aInstrument )
    {
        removeFromSequencer();
        _instrument = aInstrument;
        addToSequencer();
    }
    position = aPosition;
    length   = aLength;

    // is this event caching ?

    if ( AudioEngineProps::EVENT_CACHING && !_cachingCompleted ) _cancel = true;

    if ( _rendering )
        _update = true;     // we're rendering, request update from within render loop
    else
        updateProperties(); // instant update as we're not rendering
}

void BaseSynthEvent::unlock()
{
    _locked = false;

    if ( _updateAfterUnlock )
        calculateBuffers();

    _updateAfterUnlock = false;
}

void BaseSynthEvent::calculateBuffers()
{
    if ( _locked )
    {
        _updateAfterUnlock = true;
        return;
    }

    int oldLength;

    if ( isSequenced )
    {
        _cancel = true;

        oldLength     = _sampleLength;
        _sampleLength = ( int )( length * ( float ) AudioEngine::bytes_per_tick );
        _sampleStart  = position * AudioEngine::bytes_per_tick;
        _sampleEnd    = _sampleStart + _sampleLength;
    }
    else {
        // quick releases of a noteOn-instruction should ring for at least a 64th note
        _minLength    = AudioEngine::bytes_per_bar / 64;
        _sampleLength = AudioEngine::bytes_per_bar;     // important for amplitude swell in
        oldLength     = AudioEngineProps::BUFFER_SIZE;  // buffer is as long as the engine's buffer size
        _hasMinLength = false;                          // keeping track if the min length has been rendered
    }

    _adsr->setBufferLength( _sampleLength );

    // sample length changed (f.i. tempo change) or buffer not yet created ?
    // create buffer for (new) sample length
    if ( _sampleLength != oldLength || _buffer == 0 )
    {
        // note that when event caching is enabled, the buffer is as large as
        // the total event length requires

        if ( AudioEngineProps::EVENT_CACHING && isSequenced )
        {
            destroyBuffer(); // clear previous buffer contents
            _buffer = new AudioBuffer( AudioEngineProps::OUTPUT_CHANNELS, _sampleLength );
        }
        else
            _buffer = new AudioBuffer( AudioEngineProps::OUTPUT_CHANNELS, AudioEngineProps::BUFFER_SIZE );
    }

    if ( AudioEngineProps::EVENT_CACHING && isSequenced )
    {
        resetCache(); // yes here, not in cache()-invocation as cancels might otherwise remain permanent (see BulkCacher)

        if ( _autoCache && !_caching ) // re-cache
            cache( false );
    }
}

/**
 * synthesize is invoked by the Sequencer for rendering a non-sequenced
 * BaseSynthEvent into a single buffer
 *
 * aBufferLength {int} length of the buffer to synthesize
 */
AudioBuffer* BaseSynthEvent::synthesize( int aBufferLength )
{
    // in case buffer length is unequal to cached length, create new write buffer
    if ( aBufferLength != AudioEngineProps::BUFFER_SIZE || _buffer == 0 )
    {
        destroyBuffer();
        _buffer = new AudioBuffer( AudioEngineProps::OUTPUT_CHANNELS, aBufferLength );
    }
    render( _buffer ); // synthesize, also overwrites old buffer contents

    // keep track of the rendered bytes, in case of a key up event
    // we still want to have the sound ring for the minimum period
    // defined in the constructor instead of cut off immediately

    if ( _queuedForDeletion && _minLength > 0 )
        _minLength -= aBufferLength;

    if ( _minLength <= 0 )
    {
        _hasMinLength = true;
        setDeletable( _queuedForDeletion );

        // this event is about to be deleted, apply a tiny fadeout
        if ( _queuedForDeletion )
        {
            int amt = ceil( aBufferLength / 4 );

            SAMPLE_TYPE envIncr = MAX_PHASE / amt;
            SAMPLE_TYPE amp     = MAX_PHASE;

            for ( int i = aBufferLength - amt; i < aBufferLength; ++i )
            {
                for ( int c = 0, nc = _buffer->amountOfChannels; c < nc; ++c )
                    _buffer->getBufferForChannel( c )[ i ] *= amp;

                amp -= envIncr;
            }
        }
    }
    return _buffer;
}

/**
 * (pre-)cache the contents of the BaseSynthEvent in its entirety
 * this can be done in idle time to make optimum use of resources
 */
void BaseSynthEvent::cache( bool doCallback )
{
    if ( _buffer == 0 ) return; // cache request likely invoked after destruction

    _caching = true;

    doCache();

    if ( doCallback )
        sequencer::bulkCacher->cacheQueue();
}

ADSR* BaseSynthEvent::getADSR()
{
    return _adsr;
}

float BaseSynthEvent::getVolume()
{
    return _volume;
}

void BaseSynthEvent::setVolume( float aValue )
{
    _volume = aValue;
}

/* protected methods */

/**
 * actual updating of the properties, requested by invalidateProperties
 * this operation might potentially delete objects that could be in
 * use during a rendering operation, as such it is invoked from the render
 */
void BaseSynthEvent::updateProperties()
{
    // ADSR envelopes

    _adsr->cloneEnvelopes( _instrument->adsr );
    calculateBuffers();

    _update = false; // we're in sync now :)
    _cancel = false;
}

/**
 * the actual synthesizing of the audio
 *
 * @param aOutputBuffer {AudioBuffer*} the buffer to write into
 */
void BaseSynthEvent::render( AudioBuffer* aOutputBuffer )
{
    // override in your custom subclass, note that
    // the body of the function will look somewhat like this:

    _rendering = true;

    int bufferLength      = aOutputBuffer->bufferSize;
    int renderStartOffset = AudioEngineProps::EVENT_CACHING && isSequenced ? _cacheWriteIndex : 0;

    int maxSampleIndex  = _sampleLength - 1;                // max index possible for this events length
    int renderEndOffset = renderStartOffset + bufferLength; // max buffer index to be written to in this cycle

    // keep in bounds of event duration
    if ( renderEndOffset > maxSampleIndex )
    {
        renderEndOffset = maxSampleIndex;
        aOutputBuffer->silenceBuffers(); // as we tend to overwrite the incoming buffer
    }

    int i;
    for ( i = renderStartOffset; i < renderEndOffset; ++i )
    {
        // render math goes here
        SAMPLE_TYPE amp = 0.0;

        // end of single render iteration, write into output buffer goes here :

        if ( _update ) updateProperties(); // if an update was requested, do it now (prior to committing to buffer)
        if ( _cancel ) break;              // if a caching cancel was requested, cancel now

        // -- write the output into the buffers channels

        for ( int c = 0, ca = aOutputBuffer->amountOfChannels; c < ca; ++c )
            aOutputBuffer->getBufferForChannel( c )[ i ] = amp * _volume;
    }

    // apply ADSR envelopes and update cacheWriteIndex for next render cycle
    _adsr->apply( aOutputBuffer, _cacheWriteIndex );
    _cacheWriteIndex += i;

    if ( AudioEngineProps::EVENT_CACHING )
    {
        if ( isSequenced )
        {
            _caching = false;

            // was a cancel requested ? re-cache to match the new instrument properties (cancel
            // may only be requested during the changing of properties!)
            if ( _cancel )
            {
                //calculateBuffers(); // no, use a manual invocation (BulkCacher)
            }
            else
            {
                if ( i == maxSampleIndex )
                    _cachingCompleted = true;

                if ( _bulkCacheable )
                    _autoCache = true;
            }
        }
    }
    _cancel    = false; // ensure we can render for the next iteration
    _rendering = false;
}

void BaseSynthEvent::setDeletable( bool value )
{
    // pre buffered event or rendered min length ? schedule for immediate deletion

    if ( isSequenced || _hasMinLength )
        _deleteMe = value;
    else
        _queuedForDeletion = value;
}

/**
 * @param aInstrument pointer to the SynthInstrument containing the rendering properties for the BaseSynthEvent
 * @param aFrequency  frequency in Hz for the note to be rendered
 * @param aPosition   offset in the sequencer where this event starts playing / becomes audible
 * @param aLength     length of the event (in sequencer steps)
 * @param aIsSequenced whether this event is sequenced and only audible in a specific sequence range
 */
void BaseSynthEvent::init( SynthInstrument *aInstrument, float aFrequency, int aPosition,
                       int aLength, bool aIsSequenced )
{
    _destroyableBuffer = true;  // synth event buffer is always unique and managed by this instance !
    _instrument        = aInstrument;
    _adsr              = aInstrument->adsr != 0 ? aInstrument->adsr->clone() : new ADSR();

    // when instrument has no fixed length and the decay is short
    // we deactivate the decay envelope completely (for now)
    if ( !aIsSequenced && _adsr->getDecay() < .75 )
        _adsr->setDecay( 0 );

    _buffer                = 0;
    _locked                = false;
    position               = aPosition;
    length                 = aLength;
    isSequenced            = aIsSequenced;
    _queuedForDeletion     = false;
    _deleteMe              = false;
    _update                = false;
    _cancel                = false;       // whether we should cancel caching
    _hasMinLength          = isSequenced; // a sequenced event has no early cancel
    _caching               = false;
    _cachingCompleted      = false; // whether we're done caching
    _autoCache             = false; // we'll cache sequentially instead
    _rendering             = false;
    _volume                = aInstrument->volume;
    _sampleLength          = 0;
    _cacheWriteIndex       = 0;

    setFrequency( aFrequency );

    calculateBuffers();
    addToSequencer();
}

void BaseSynthEvent::addToSequencer()
{
    // adds the event to the sequencer so it can be heard

    if ( isSequenced )
        _instrument->audioEvents->push_back( this );
    else
        _instrument->liveAudioEvents->push_back( this );
}

void BaseSynthEvent::removeFromSequencer()
{
    if ( !isSequenced )
    {
        for ( int i = 0; i < _instrument->liveAudioEvents->size(); i++ )
        {
            if ( _instrument->liveAudioEvents->at( i ) == this )
            {
                _instrument->liveAudioEvents->erase( _instrument->liveAudioEvents->begin() + i );
                break;
            }
        }
    }
    else
    {
        for ( int i = 0; i < _instrument->audioEvents->size(); i++ )
        {
            if ( _instrument->audioEvents->at( i ) == this )
            {
                _instrument->audioEvents->erase( _instrument->audioEvents->begin() + i );
                break;
            }
        }
    }
}

/**
 * render the event into the buffer and cache its
 * contents for the given bufferLength (we can cache
 * buffer fragments on demand, or all at once)
 */
void BaseSynthEvent::doCache()
{
    render( _buffer );
}
