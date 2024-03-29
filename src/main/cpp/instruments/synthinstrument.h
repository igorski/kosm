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
#ifndef __SYNTHINSTRUMENT_H_INCLUDED__
#define __SYNTHINSTRUMENT_H_INCLUDED__

#include "baseinstrument.h"
#include "../audiochannel.h"
#include <events/baseaudioevent.h>
#include <modules/arpeggiator.h>
#include <modules/adsr.h>
#include <modules/routeableoscillator.h>

class SynthInstrument : public BaseInstrument
{
    public:
        SynthInstrument();
        ~SynthInstrument();

        int waveform;
        int octave;
        int keyboardOctave;
        ADSR* adsr;
        float keyboardVolume;
        RouteableOscillator *rOsc;

        bool osc2active;
        int osc2waveform;
        float osc2detune;
        int osc2octaveShift;
        int osc2fineShift;

        Arpeggiator* arpeggiator;
        bool arpeggiatorActive;

        std::vector<BaseAudioEvent*>* audioEvents;
        std::vector<BaseAudioEvent*>* liveAudioEvents;

        // overrides

        bool hasEvents();
        bool hasLiveEvents();
        void updateEvents();  // updates all events after changing synth properties
        void clearEvents();
        bool removeEvent( BaseAudioEvent* aEvent );

        std::vector<BaseAudioEvent*>* getEvents();
        std::vector<BaseAudioEvent*>* getLiveEvents();

    protected:
        void init();
};

#endif
