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
#ifndef __LFO_H_INCLUDED__
#define __LFO_H_INCLUDED__

#include "global.h"
#include "wavetable.h"

class LFO
{
    public:
        LFO();
        ~LFO();

        static constexpr double MAX_LFO_RATE  = 10;  // the maximum rate of oscillation in Hz
        static constexpr double MIN_LFO_RATE  = .1;  // the minimum rate of oscillation in Hz
        static const int WAVE_TABLE_PRECISION = 128; // the amount of samples in the wave table

        float getRate();
        void setRate( float value );
        int getWave();
        void setWave( int value );

        void generate();
        WaveTable* getTable();

    protected:

        SAMPLE_TYPE _rate;

        int _wave;
        WaveTable* _table;

        int calculateBufferLength( float aMinRate );
};

#endif
