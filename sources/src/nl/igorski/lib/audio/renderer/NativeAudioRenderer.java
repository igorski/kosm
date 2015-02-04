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
package nl.igorski.lib.audio.renderer;

import android.content.Context;
import android.os.Build;
import nl.igorski.lib.audio.definitions.WaveTables;
import nl.igorski.lib.audio.interfaces.ISequencer;
import nl.igorski.lib.audio.nativeaudio.*;
import nl.igorski.lib.interfaces.listeners.IBounceListener;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.threading.BaseThread;
import nl.igorski.lib.utils.time.Timeout;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 10-04-12
 * Time: 20:16
 * To change this template use File | Settings | File Templates.
 */
public final class NativeAudioRenderer extends BaseThread
{
    private static NativeAudioRenderer INSTANCE;

    private ISequencer   _sequencer;  // reference to the (visual) sequencer managing all audio events and the session
    private SequencerAPI _api;       // hold reference (prevents garbage collection) to native sequencer providing audio

    /* audio generation related, calculated on platform-specific basis in constructor */

    public static int SAMPLE_RATE = 44100;
    public static int BUFFER_SIZE = 2048;

    /* time related */

    public static int TIME_SIG_BEAT_AMOUNT  = 4; // upper numeral in time signature (i.e. the "3" in 3/4)
    public static int TIME_SIG_BEAT_UNIT    = 4; // lower numeral in time signature (i.e. the "4" in 3/4)
    private float _tempo;

    // we CAN multiply the output the volume to decrease it, preventing rapidly distorting audio ( especially on filters )
    private static final float VOLUME_MULTIPLIER = 1;
    private static float _volume = .85f /* assumed default level */ * VOLUME_MULTIPLIER;

    // we make these available across classes

    public static int BYTES_PER_SAMPLE = 8;
    public static int BYTES_PER_BEAT   = 22050;
    public static int BYTES_PER_BAR    = 88200;
    public static int BYTES_PER_TICK   = 5512;

    // handle sequencer positions

    private int _stepPosition = 0;

    /* recording buffer specific */

    private boolean _recordOutput = false;
    private IBounceListener _listener;

    private boolean _openSLrunning     = false;
    private boolean _initialCreation   = true;
    private boolean _disposed          = false;
    private boolean _tablesCached      = false;
    private int _openSLRetry           = 0;

    /**
     * AudioRenderer synthesizes all audio
     * created in the sequencer
     *
     * @param aContext   {Context} current application context
     * @param aSequencer {ISequencer} the sequencer containing
     *        all the audio events to be synthesized
     */
    public NativeAudioRenderer( Context aContext, ISequencer aSequencer )
    {
        INSTANCE   = this;
        _sequencer = aSequencer;

        initJNI();

        //setPriority( MAX_PRIORITY ); // no need, all in native layer
    }

    /* public methods */

    // (re-)registers interface to match current/updated JNI environment
    public void initJNI()
    {
        NativeAudioEngine.init();
    }

    public void createOutput( int aSampleRate, int aBufferSize )
    {
        SAMPLE_RATE = aSampleRate;
        BUFFER_SIZE = aBufferSize;

        // Android emulators can only work at 8 kHz or crash...
        if ( Build.FINGERPRINT.startsWith( "generic" ))
            SAMPLE_RATE = 8000;

        _api = new SequencerAPI();
        _api.prepare( BUFFER_SIZE, SAMPLE_RATE, 120.0f, TIME_SIG_BEAT_AMOUNT, TIME_SIG_BEAT_UNIT ); // start w/ default of 120 BPM in 4/4 time

        _disposed = false;
    }

    /**
     * set play / pause state
     * @param value {boolean} whether sequencer is playing (true)
     *                        or paused (false)
     */
    public void setPlaying( boolean value )
    {
        _api.setPlaying( value );
    }

    public void rewind()
    {
        _api.rewind();
    }

    public void setBouncing( boolean value, String outputDirectory )
    {
        _api.setBounceState( value, calculateMaxBuffers(), outputDirectory );
    }

    public float getTempo()
    {
        return _tempo;
    }

    public SequencerAPI getAPI()
    {
        return _api;
    }

    public ProcessingChain getMasterBusProcessors()
    {
        return NativeAudioEngine.getMasterBusProcessors();
    }

    /**
     * tempo changes are executed outside of the
     * render and thus are queued
     *
     * @param aValue {float} new tempo
     * @param aTimeSigBeatAmount {int} time signature beat amount
     * @param aTimeSigBeatUnit   {int} time signature beat unit
     */
    public void setTempo( float aValue, int aTimeSigBeatAmount, int aTimeSigBeatUnit )
    {
        _api.setTempo( aValue, aTimeSigBeatAmount, aTimeSigBeatUnit );
    }

    /**
     * unless the sequencer isn't running, when
     * this method can be used to set the tempo directly
     *
     * @param aValue {float} new tempo
     * @param aTimeSigBeatAmount {int} time signature beat amount
     * @param aTimeSigBeatUnit   {int} time signature beat amount
     */
    public void setTempoNow( float aValue, int aTimeSigBeatAmount, int aTimeSigBeatUnit )
    {
        _api.setTempoNow( aValue, aTimeSigBeatAmount, aTimeSigBeatUnit );
    }

    public float getVolume()
    {
        return _volume / VOLUME_MULTIPLIER;
    }

    public void setVolume( float aValue )
    {
        _volume = aValue * VOLUME_MULTIPLIER;
        _api.setVolume( _volume );
    }

    public void updateMeasures( int aValue )
    {
        _api.updateMeasures( aValue, _sequencer.getStepsPerBar());
    }

    /**
     * records the live output of the engine
     *
     * this keeps recording until setRecordingState is invoked with value false
     * given outputDirectory will contain several .WAV files each at the buffer
     * length returned by the "calculateMaxBuffers"-method
     */
    public void setRecordingState( boolean value, String outputDirectory )
    {
        int maxRecordBuffers = 0;

        // create / reset the recorded buffer when
        // hitting the record button
        if ( value )
            maxRecordBuffers = calculateMaxBuffers();

        _recordOutput = value;
        _api.setRecordingState( _recordOutput, maxRecordBuffers, outputDirectory );
    }

    /**
     * records the input channel of the Android device, note this can be done
     * while the engine is running a sequence / synthesizing audio
     *
     * given outputDirectory will contain a .WAV file at the buffer length
     * representing given maxDurationInMilliSeconds
     */
    public void setRecordFromDeviceInputState( boolean value, String outputDirectory, int maxDurationInMilliSeconds )
    {
        int maxRecordBuffers = 0;

        // create / reset the recorded buffer when
        // hitting the record button

        if ( value )
            maxRecordBuffers = BufferUtility.millisecondsToBuffer( maxDurationInMilliSeconds, SAMPLE_RATE );

        _recordOutput = value;
        _api.setRecordingFromDeviceState( _recordOutput, maxRecordBuffers, outputDirectory );
    }

    public boolean getRecordingState()
    {
        return _recordOutput;
    }

    /**
     * define a range for the sequencer to loop
     * @param aStartPosition {int} buffer position of the start point
     * @param aEndPosition   {int} buffer position of the end point
     */
    public void setLoopPoint( int aStartPosition, int aEndPosition )
    {
        _api.setLoopPoint( aStartPosition, aEndPosition, _sequencer.getStepsPerBar());
    }

    /**
     * get the sequencers current position (is a step point
     * between the sequencers loop range), we should never
     * return buffer positions as it is the AudioRenderers
     * job to handle these
     *
     * @return {int}
     */
    public int getPosition()
    {
        return _stepPosition;
    }

    @Override
    public void start()
    {
        if ( !_isRunning )
        {
            super.start();
        }
        else
        {
            initJNI();  // update reference to this Java object in JNI
            unpause();
        }
    }

    @Override
    public void unpause()
    {
        initJNI();
        super.unpause();
    }

    public void reset()
    {
        NativeAudioEngine.reset();
        _openSLRetry = 0;
    }

    // due to Object pooling we keep the thread alive by just pausing its execution, NOT actual cleanup
    // exiting the application will kill the native library anyways
    @Override
    public void dispose()
    {
        pause();

        _disposed      = true;
        _openSLrunning = false;

        NativeAudioEngine.stop();   // halt the Native audio thread

        //super.dispose();          // nope, as that will actually halt THIS thread
    }

    public void setBounceListener( IBounceListener aListener )
    {
        _listener = aListener;
    }

    public void run()
    {
        //DebugTool.log( "NativeAudioRenderer STARTING NATIVE AUDIO RENDER THREAD" );

        handleThreadStartTimeout();

        while ( _isRunning )
        {
            // start the Native audio thread
            if ( !_openSLrunning )
            {
                DebugTool.log( "NativeAudioRenderer STARTING NATIVE THREAD @ " + SAMPLE_RATE + " Hz using " + BUFFER_SIZE + " samples per buffer" );

                _openSLrunning = true;
                NativeAudioEngine.start();
            }

            // the remainder of this function body is actually blocked
            // as long as the native thread is running

            DebugTool.log( "NativeAudioRenderer THREAD STOPPED" );

            _openSLrunning = false;

            synchronized ( _pauseLock )
            {
                while ( _paused )
                {
                    try
                    {
                        _pauseLock.wait();
                    }
                    catch ( InterruptedException e ) {}
                }
            }
        }
    }

    private int calculateMaxBuffers()
    {
        // we record a maximum of 30 seconds before invoking the "handleRecordingUpdate"-method on the sequencer
        final double amountOfMinutes = .5;

        return BufferUtility.millisecondsToBuffer(( int ) ( amountOfMinutes * 60000 ), SAMPLE_RATE );
    }

    /**
     * rare bug : occasionally the audio engine won't start, closing / reopening
     * the application tends to work....
     *
     * this poor man's check checks whether the bridge has submitted its connection
     * message from the native layer after a short timeout
     */
    private void handleThreadStartTimeout()
    {
        if ( !_openSLrunning )
        {
            Timeout.setTimeout( 2000, new Runnable()
            {
                public void run()
                {
                    if ( !_disposed && !_openSLrunning )
                    {
                        DebugTool.log( "NativeAudioRenderer PANIC :: could not start engine" );
                        _sequencer.handleEnginePanic();
                    }
                }

            }, true );
        }
    }

    /**
     * queries whether we can try to restart the engine
     * in case an error has occurred, note this will also
     * increment the amount of retries
     *
     * @return {boolean}
     */
    private boolean canRetry()
    {
        return ++_openSLRetry < 5;
    }

    /* native bridge methods */

    /**
     * all these methods are static and provide a bridge from C++ back into Java
     * these methods are used by the native audio engine for updating states and
     * requesting data
     *
     * Java method IDs need to be supplied to C++ in order te make the callbacks, you
     * can discover the IDs by building the Java project and running the following
     * command in the output /bin folder:
     *
     * javap -s -private -classpath classes nl.igorski.lib.audio.renderer.NativeAudioRenderer
     */
    public static void handleBridgeConnected( int aSomething )
    {
        // cache wave tables

        if ( !INSTANCE._tablesCached )
        {
            new Thread(
            new Runnable()
            {
                public void run()
                {
                    INSTANCE._tablesCached = true;

                    // see enumerated WaveForms (global.h) and WAVE_TABLE_PRECISION (lfo.h)
                    JNISampleManager.cacheTable( WaveTables.SINE.length,     0, WaveTables.SINE );
                    JNISampleManager.cacheTable( WaveTables.TRIANGLE.length, 1, WaveTables.TRIANGLE );
                    JNISampleManager.cacheTable( WaveTables.SAWTOOTH.length, 2, WaveTables.SAWTOOTH );
                    JNISampleManager.cacheTable( WaveTables.SQUARE.length,   3, WaveTables.SQUARE );

                    DebugTool.log( "CACHED WAVE TABLES" );
                }
            }).start();
        }
    }

    public static void handleTempoUpdated( float aNewTempo, int aBytesPerBeat, int aBytesPerTick, int aBytesPerBar, int aTimeSigBeatAmount, int aTimeSigBeatUnit )
    {
        INSTANCE._tempo = aNewTempo;

        BYTES_PER_BEAT  = aBytesPerBeat;
        BYTES_PER_TICK  = aBytesPerTick;
        BYTES_PER_BAR   = aBytesPerBar;

        TIME_SIG_BEAT_AMOUNT = aTimeSigBeatAmount;
        TIME_SIG_BEAT_UNIT   = aTimeSigBeatUnit;

        INSTANCE._sequencer.handleTempoUpdate();

        // weird bug where on initial start sequencer would not know the step range...
        if ( INSTANCE._initialCreation )
        {
            INSTANCE._initialCreation = false;
            INSTANCE.setLoopPoint( 0, BYTES_PER_BAR );
        }
        //DebugTool.log( "update tempo @ " + aNewTempo + " w/ bytes per beat " + aBytesPerBeat + " bytes per tick " + aBytesPerTick + " bytes per bar " + aBytesPerBar + " time signature > " + TIME_SIG_BEAT_AMOUNT + "/" + TIME_SIG_BEAT_UNIT );
    }

    public static void handleSequencerPositionUpdate( int aStepPosition )
    {
        INSTANCE._stepPosition = aStepPosition;
        INSTANCE._sequencer.handleSequencerPositionUpdate( aStepPosition );
    }

    public static void handleRecordingUpdate( int aRecordedFileNum )
    {
        INSTANCE._sequencer.handleRecordingUpdate( false, aRecordedFileNum );
    }

    public static void handleBounceComplete( int aIdentifier )
    {
        if ( INSTANCE._listener != null )
            INSTANCE._listener.handleBounceComplete( true );

        INSTANCE._listener = null; // garbage collect the command
    }

    public static void handleOpenSLError()
    {
        DebugTool.log( "NativeAudioRenderer::ERROR > received Open SL error callback from native layer" );

        // re-initialize thread
        if ( INSTANCE.canRetry() )
        {
            INSTANCE.dispose();
            INSTANCE._openSLrunning = false;
            INSTANCE.createOutput( SAMPLE_RATE, BUFFER_SIZE );
            INSTANCE.start();
        }
        else {
            DebugTool.log( "exceeded maximum amount of retries. Cannot continue using NativeaudioRenderer" );
        }
    }
}
