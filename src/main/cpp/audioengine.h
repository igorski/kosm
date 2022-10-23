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
#ifndef __AUDIOENGINE_H_INCLUDED__
#define __AUDIOENGINE_H_INCLUDED__

#include "audiobuffer.h"
#include "audiochannel.h"
#include "global.h"
#include "processingchain.h"

/**
 * this is the API exposed to an external application
 * for interacting with the AudioEngine (it does nothing
 * more than prepare and start / stop / reset the render thread
 */
namespace AudioEngine
{
    /* public methods */

#ifdef USE_JNI
    void init();
#endif
    void start();
    void stop();
    void reset();

    /* engine properties */

    extern int bytes_per_beat;      // the amount of samples necessary for a single beat at the current tempo and sample rate
    extern int bytes_per_bar;       // the amount of samples for a full bar at the current tempo and sample rate
    extern int bytes_per_tick;      // the amount of samples per sub division (e.g. 16th note)

    extern int amount_of_bars;         // the amount of measures in the current sequencer
    extern int beat_subdivision;       // the amount of sub divisions the engine recognises for a beat (for instance a value of 4 equals sixteenth notes in 4/4 time)
    extern int min_buffer_position;    // the lowest sample offset in the current loop range
    extern int max_buffer_position;    // the maximum sample offset in the current loop range
    extern int marked_buffer_position; // the buffer position that should launch a notification when playback exceeds this position
    extern int min_step_position;      // the lowest step in the current sequence
    extern int max_step_position;      // the maximum step in the current sequence (e.g. 16)
    extern bool playing;               // whether the engine is playing or paused
    extern bool recordOutput;          // whether to record rendered output
    extern bool haltRecording;         // whether to stop the recording upon next iteration
    extern bool bouncing;              // whether bouncing audio (i.e. rendering in inaudible offline mode without thread lock)
    extern int recordingIterator;
    extern int recordingMaxIterations;
    extern int recordingFileId;
    extern bool recordFromDevice;   // whether to record audio from the Android devices input
    extern bool monitorRecording;   // whether to make recorded audio audible in the output channel

    /* buffer read/write pointers */

    extern int bufferPosition;      // the current sequence position in samples "playback head" offset ;-)
    extern int stepPosition;        // the current sequence bar subdivided position (e.g. 16th note of a bar)
    extern int playbackPos;

    /* tempo related */

    extern float tempo;                     // the tempo of the sequencer
    extern float queuedTempo;               // the tempo the sequencer will move to once current render cycle completes
    extern int time_sig_beat_amount;        // time signature upper numeral (i.e. the "3" in 3/4)
    extern int time_sig_beat_unit;          // time signature lower numeral (i.e. the "4" in 3/4)
    extern int queuedTime_sig_beat_amount;  // the time signature beat amount the sequencer moves to on next cycle
    extern int queuedTime_sig_beat_unit;    // the time signature beat unit the sequencer moves to on next cycle

    /* output related */

    extern float volume;                // master volume
    extern ProcessingChain* masterBus;  // processing chain for the master bus

    /* internal methods */

    void handleTempoUpdate            ( float aQueuedTempo, bool broadcastUpdate );
    void handleSequencerPositionUpdate( float streamTimeStamp );
    bool writeChannelCache            ( AudioChannel* channel, AudioBuffer* channelBuffer, int cacheReadPos );
}
#endif
