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
#ifndef __AUDIOPARTICLEEVENT_H_INCLUDED__
#define __AUDIOPARTICLEEVENT_H_INCLUDED__

#include <events/synthevent.h>

class PercussionTypes
{
    public:
        enum Types {
            KICK_808 = 10,
            STICK    = 11,
            SNARE    = 12,
            HI_HAT   = 13
        };
};

class AudioParticleEvent : public SynthEvent
{
    public:
        AudioParticleEvent( float aFrequency, int aPosition,
                            float aLength, SynthInstrument* aInstrument );
        AudioParticleEvent( float aFrequency, SynthInstrument* aInstrument,
                            float aLength, float aAttack, float aRelease );
        ~AudioParticleEvent();

        void setFrequency( float aFrequency, bool allOscillators, bool storeAsBaseFrequency );
        void calculateBuffers();
        void reset();
        void destroy();

    protected:
        void render( AudioBuffer* aOutputBuffer );

        static constexpr SAMPLE_TYPE StickDecayFactor = 0.590;
};

#endif
