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
#ifndef __AUDIOCHANNEL_H_INCLUDED__
#define __AUDIOCHANNEL_H_INCLUDED__

#include "audiobuffer.h"
#include "processingchain.h"
#include <events/baseaudioevent.h>
#include <vector>

class AudioChannel
{
    public:

        AudioChannel( float aMixVolume );
        AudioChannel( float aMixVolume, int aMaxBufferPosition );
        ~AudioChannel();

        // queried and modified by sequencer
        std::vector<BaseAudioEvent*> audioEvents;
        std::vector<BaseAudioEvent*> liveEvents;

        ProcessingChain *processingChain;

        bool hasLiveEvents;
        bool isMono;
        bool muted;
        bool hasCache;
        bool isCaching;
        float mixVolume;

        void addEvent( BaseAudioEvent* aEvent );

        /**
         * a channel can both contain buffered output as well
         * as live events ( for instance a keyboard plays the
         * same synthesizer as the sequenced patterns ). We need to
         * add them here so they can benefit from the same processing chain
         *
         * @param aLiveEvent {BaseAudioEvent} the live event
         */
        void addLiveEvent( BaseAudioEvent* aLiveEvent );

        /**
         * an AudioChannel may contain audio that spans a different
         * buffer range than the other channels in a Sequencer
         * (for instance: a looping one bar drum pattern over
         * four bars of synthesis)
         *
         * specify a max buffer position for the AudioChannel
         * to have the AudioRenderer loop its contents while
         * processing the whole buffer range for the other channels
         *
         * if the max buffer position is equal to the sequencer
         * max range, pass no value or 0 for maxBufferPosition
         */
        int maxBufferPosition;

        /**
         * clears all references to previously linked events
         */
        void reset();

        void readCachedBuffer( AudioBuffer* aOutputBuffer, int aReadOffset );
        bool canCache();
        void canCache( bool value, int aBufferSize, int aCacheStartOffset, int aCacheEndOffset );
        void clearCachedBuffer();
        void writeCache( AudioBuffer* aBuffer, int aReadOffset );

        // to spare CPU resources an AudioChannel can cache its contents

    protected:

        void init();
        AudioBuffer* _cachedBuffer;
        bool _canCache;
        int _cacheReadPointer;
        int _cacheWritePointer;

        // the cache start and end offset specify at which buffer offset
        // the sequencer must read from the cache
        int _cacheStartOffset;
        int _cacheEndOffset;
};

#endif
