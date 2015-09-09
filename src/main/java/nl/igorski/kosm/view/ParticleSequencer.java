package nl.igorski.kosm.view;

import android.content.DialogInterface;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.kosm.config.Config;
import nl.igorski.kosm.controller.effects.ToggleDelayCommand;
import nl.igorski.kosm.controller.effects.ToggleDistortionCommand;
import nl.igorski.kosm.controller.effects.ToggleFilterCommand;
import nl.igorski.kosm.controller.render.WriteRecordingCommand;
import nl.igorski.kosm.controller.sequencer.ToggleSequencerModeCommand;
import nl.igorski.kosm.controller.startup.CreateMasterBusCommand;
import nl.igorski.kosm.controller.system.RestartSequencerCommand;
import nl.igorski.kosm.definitions.KosmConstants;
import nl.igorski.kosm.model.KosmInstruments;
import nl.igorski.kosm.model.SettingsProxy;
import nl.igorski.kosm.model.definitions.SettingsDefinitions;
import nl.igorski.kosm.model.vo.ParticleEmitterVO;
import nl.igorski.kosm.model.vo.VOSetting;
import nl.igorski.kosm.util.cache.CacheWriter;
import nl.igorski.kosm.view.physics.components.ParticleEmitter;
import nl.igorski.lib.audio.definitions.AudioConstants;
import nl.igorski.lib.audio.definitions.WaveTables;
import nl.igorski.lib.audio.helpers.DevicePropertyCalculator;
import nl.igorski.lib.audio.interfaces.IUpdateableInstrument;
import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.audio.nativeaudio.JavaUtilities;
import nl.igorski.lib.audio.nativeaudio.Notifications;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.math.MathTool;
import nl.igorski.kosm.R;
import nl.igorski.kosm.definitions.ParticleSounds;
import nl.igorski.kosm.view.ui.ViewRenderer;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import nl.igorski.lib.utils.notifications.Confirm;
import nl.igorski.kosm.view.physics.components.AudioParticle;
import nl.igorski.lib.utils.notifications.Alert;
import nl.igorski.lib.utils.storage.FileSystem;
import nl.igorski.lib.utils.ui.ButtonTool;

import java.io.File;
import java.util.Vector;

public final class ParticleSequencer extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener,
                                                                    IUpdateableInstrument
{
    /* threads */

    private ViewRenderer _renderer;
    private MWEngine     _mwengine;

    public boolean threadsActive = false;

    /* view related */

    public static int width;
    public static int height;

    /* UI buttons */

    private ImageButton buttonKick;
    private ImageButton buttonSaw;
    private ImageButton buttonSine;
    private ImageButton buttonSnare;
    private ImageButton buttonTwang;

    /* model */

    private SettingsProxy _settingsProxy;

    /* audio related */

    public static MWProcessingChain  masterBus;   // strong reference to avoid C++ destructors being invoked
    private KosmInstruments _instruments; // strong reference to avoid C++ destructors being invoked
    private MWProcessingChain       _filterBus;
    private MWProcessingChain       _formantBus;
    private Vector<AudioParticle>   _particles;
    private Vector<ParticleEmitter> _emitters;

    private long _lastEmitterUpdate = 0l;

    public ParticleSounds            particleSound = ParticleSounds.PARTICLE_SINE;

    /* device coordinates */

    public float pitch;
    public float roll;
    public float azimuth;

    /* used for transforming the rotation across devices */

    private boolean ready         = false;
    private float[] gravityValues = new float[ 3 ];
    private float[] geomagValues  = new float[ 3 ];
    private float[] inR           = new float[ 9 ];
    private float[] outR          = new float[ 9 ];
    private float[] prefValues    = new float[ 3 ];

    private Display _display;
    private int deviceRot; // prevent garbage collector hitting us by strongly referencing it

    // TODO: enter the PROPER remapCoordinates in the switch in the touch handler-method
    // (if people start complaining) this is a "easy fix" for inverting the values
    // for landscape based devices (i.e. tablets)
    private boolean tabletInverse = false;

    private static ParticleSequencer INSTANCE;

    public ParticleSequencer( Context context, AttributeSet attrs )
    {
        super( context, attrs );

        INSTANCE = this;

        _settingsProxy = ( SettingsProxy ) Core.registerProxy( new SettingsProxy( getContext()));

        _particles   = new Vector<AudioParticle>();
        _emitters    = new Vector<ParticleEmitter>();
        _instruments = new KosmInstruments( this );

        _formantBus  = KosmInstruments.getInstrumentByParticleSound( ParticleSounds.PARTICLE_KICK ).processingChain;
        _filterBus   = KosmInstruments.getInstrumentByParticleSound( ParticleSounds.PARTICLE_SINE ).processingChain;

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback( this );

        WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
        _display         = wm.getDefaultDisplay();
        width            = _display.getWidth();
        height           = _display.getHeight();

        // register to receive sensor events
        SensorManager sm = ( SensorManager ) context.getSystemService( Context.SENSOR_SERVICE );
        sm.registerListener( this, sm.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),  SensorManager.SENSOR_DELAY_GAME );
        sm.registerListener( this, sm.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ), SensorManager.SENSOR_DELAY_GAME );

        setFocusable( true ); // make sure we get key events
    }

    /* public methods */

    public MWEngine getAudioEngine()
    {
        return _mwengine;
    }

    public ViewRenderer getViewRenderer()
    {
        return _renderer;
    }

    public boolean usePrecaching()
    {
        return false; // nope!
    }

    public void handleEnginePanic()
    {
        Core.notify( new RestartSequencerCommand() );
    }

    public void updateInstrument( InternalSynthInstrument instrument )
    {
        //prepareRecache(); // make sure we're not auto caching ALL events and hogging all CPU resources

        // update audio event properties to match changes in the instrument properties

//        final Vector<BaseCacheableAudioEvent> events = _audioEvents[ instrument.index ];
//        for ( final BaseAudioEvent vo : events )
//        {
//            final SynthEvent event = ( SynthEvent ) vo;
//            event.invalidateProperties( event.getPosition(), event.getLength(),
//                                        _voSongData.instruments.get( instrument.index ).instrument/*, state*/ );
//        }

        // make sure the audio events in the current measure are cached instantaneously to prevent awkward silences
        // the remaining events in other measures are cached upon reaching the end of the currently playing measure
        //cacheAudioEventsForMeasure( -1 );
    }

    /**
     * called periodically by the NativeAudioRenderer when a new fragment has been recorded
     */
    public void handleRecordingUpdate( final boolean isComplete, int aRecordingFileNum )
    {
        CacheWriter.renameLastFragment(aRecordingFileNum);
        handleRecordingUpdate(isComplete);
    }

    /**
     * called when the user manually deactivates recording
     *
     * @param isComplete {boolean} whether we have completed recording, this true when the user has
     *                             manually deactivated the recording state (the AudioRenderer fires an
     *                             update which will always pass boolean false)
     */
    public void handleRecordingUpdate( final boolean isComplete )
    {
        // when writing updates, we write them to a cache folder
        // instead of creating a WAV file on the fly, the writing
        // occurs in the native audio thread

        // can we record the next 30 seconds ?
        if ( !isComplete )
        {
            if ( !Config.canRecord())
            {
                // altering button image throws exception when executed from outside thread !!
                _mwengine.setRecordingState(false, "");
                requestRecordingSave    ( false );
            }
        }
        else {
            requestRecordingSave( true );
        }
    }

    /* internal class : monitors state changes of the audio engine */

    private class StateObserver implements MWEngine.IObserver
    {
        // cache the enumerations (from native layer) as integer Array

        private final Notifications.ids[] _notificationEnums = Notifications.ids.values();

        public void handleNotification( int aNotificationId )
        {
            switch ( _notificationEnums[ aNotificationId ])
            {
                case ERROR_HARDWARE_UNAVAILABLE:

                    DebugTool.log( "NativeAudioRenderer::ERROR > received Open SL error callback from native layer" );

                    // re-initialize thread

                    if ( _mwengine.canRestartEngine() )
                    {
                        _mwengine.dispose();
                        _mwengine.createOutput( MWEngine.SAMPLE_RATE,
                                             MWEngine.BUFFER_SIZE );
                        _mwengine.start();
                    }
                    else {
                        DebugTool.log( "exceeded maximum amount of retries. Cannot continue using Mikrowave..." );
                    }
                    break;

                case ERROR_THREAD_START:

                    DebugTool.log( "MWEngine PANIC :: could not start engine" );
                    handleEnginePanic();
                    break;

                case STATUS_BRIDGE_CONNECTED:

                    // see enumerated WaveForms (global.h) and WAVE_TABLE_PRECISION (lfo.h)
                    JavaUtilities.cacheTable( WaveTables.SINE.length,     0, WaveTables.SINE );
                    JavaUtilities.cacheTable( WaveTables.TRIANGLE.length, 1, WaveTables.TRIANGLE);
                    JavaUtilities.cacheTable( WaveTables.SAWTOOTH.length, 2, WaveTables.SAWTOOTH);
                    JavaUtilities.cacheTable( WaveTables.SQUARE.length,   3, WaveTables.SQUARE );

                    DebugTool.log( "CACHED WAVE TABLES" );
                    break;
            }
        }

        public void handleNotification( int aNotificationId, int aNotificationValue )
        {
//            switch ( _notificationEnums[ aNotificationId ])
//            {
//                case SEQUENCER_POSITION_UPDATED:
//                    Log.d( LOG_TAG, "sequencer position : " + aNotificationValue );
//                    break;
//            }
        }
    }

    public void handleTempoUpdate()
    {
        // not used in this gravity sequencer ;)
    }

    public int getMeasures()
    {
        return 1;
    }

    public int getStepsPerBar()
    {
        return 16;
    }

    public void handleSequencerPositionUpdate( int aPosition )
    {
        // not used in this gravity sequencer ;)
    }

    public AudioParticle createAudioParticle( float xPosition, float yPosition, float radius, boolean fixed )
    {
        final AudioParticle particle = new AudioParticle( particleSound, this, xPosition, yPosition, radius, fixed );
        _particles.add( particle );

        return particle;
    }

    public AudioParticle createAudioParticle( ParticleSounds aParticleSound, float xPosition, float yPosition, float radius, boolean fixed )
    {
        final AudioParticle particle = new AudioParticle( aParticleSound, this, xPosition, yPosition, radius, fixed );
        _particles.add( particle );

        return particle;
    }

    public Vector<AudioParticle> getParticles()
    {
        return _particles;
    }

    public void removeAudioParticle( AudioParticle particle )
    {
        _particles.remove( particle );
    }

    public void createEmitter( final ParticleEmitterVO vo, final float aXPos, final float aYPos )
    {
        _emitters.add( new ParticleEmitter( vo, this, aXPos, aYPos, true ));
    }

    /**
     * perform routine step
     * for all emitter intervals
     */
    public void updateEmitters()
    {
        _lastEmitterUpdate = System.currentTimeMillis();

        for ( final ParticleEmitter emitter : _emitters )
            emitter.update( _lastEmitterUpdate );
    }

    public static void destroyFixed()
    {
        int i = INSTANCE._particles.size();

        while ( i-- > 0 )
        {
            final AudioParticle particle = INSTANCE._particles.get( i );

            if ( particle.fixed && !particle.emitter )
            {
                particle.destroy();
                INSTANCE._particles.remove( particle );
            }
        }
    }

    public static void destroyEmitters()
    {
        int i = INSTANCE._particles.size();

        while ( i-- > 0 )
        {
            final AudioParticle particle = INSTANCE._particles.get( i );

            if ( particle.emitter )
            {
                particle.destroy();
                INSTANCE._particles.remove( particle );
            }
        }
        INSTANCE._emitters.clear();
        INSTANCE._emitters = new Vector<ParticleEmitter>();
    }

    public void createRecording()
    {
        // create the filename for the recording -- we delay creation until we
        // actually have a buffer to write

        CacheWriter.createOutputFolders();   // ensure the output folders exist
        CacheWriter.flushCache();            // clear the cache folders existing contents...
    }

    /* event handlers */

    public void setRecordListener( final ImageButton aRecordButton )
    {
        aRecordButton.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                final boolean newRecordingState = !_mwengine.getRecordingState();

                if ( !newRecordingState ) // stop recording
                {
                    ButtonTool.setImageButtonImage( aRecordButton, R.drawable.icon_record );

                    // renderer has written a recording ? save it to SD card
                    handleRecordingUpdate( true );
                }
                else // start recording
                {
                    // FIRST: check if there is space available for writing 30 seconds of audio
                    if ( !Config.canRecord())
                    {
                        Alert.show( Core.getContext(), R.string.error_rec_space );
                    }
                    else
                    {
                        // just started recording ? show message
                        Alert.show( Core.getContext(), R.string.rec_started );
                        ButtonTool.setImageButtonImage( aRecordButton, R.drawable.icon_record_active );

                        createRecording();
                    }
                }
                _mwengine.setRecordingState(newRecordingState, FileSystem.getWritableRoot() + File.separator + Config.CACHE_FOLDER + File.separator);
            }
        });
    }

    public void setModeToggleListener( final Button b )
    {
        b.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                Core.notify( new ToggleSequencerModeCommand(), b );
            }
        });
    }

    public void setKickListener( ImageButton b )
    {
        buttonKick = b;

        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                deactivateParticleButtons();
                particleSound = ParticleSounds.PARTICLE_KICK;

                (( ImageButton ) v ).setImageDrawable( getResources().getDrawable( R.drawable.icon_kick_active ));
            }
        });
    }

    public void setSnareListener( ImageButton b )
    {
        buttonSnare = b;

        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                deactivateParticleButtons();
                particleSound = ParticleSounds.PARTICLE_PAD;

                (( ImageButton ) v ).setImageDrawable( getResources().getDrawable( R.drawable.icon_snare_active ));
            }
        });
    }

    public void setSawListener( ImageButton b )
    {
        buttonSaw = b;

        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                deactivateParticleButtons();
                particleSound = ParticleSounds.PARTICLE_SAW;

                (( ImageButton ) v ).setImageDrawable( getResources().getDrawable( R.drawable.icon_saw_active ));
            }
        });
    }

    public void setTwangListener( ImageButton b )
    {
        buttonTwang = b;

        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                deactivateParticleButtons();
                particleSound = ParticleSounds.PARTICLE_TWANG;

                (( ImageButton ) v ).setImageDrawable( getResources().getDrawable( R.drawable.icon_ks_active ));
            }
        });
    }

    public void setSineListener( ImageButton b )
    {
        buttonSine = b;

        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                deactivateParticleButtons();
                particleSound = ParticleSounds.PARTICLE_SINE;

                (( ImageButton ) v ).setImageDrawable( getResources().getDrawable( R.drawable.icon_sine_active ));
            }
        });
    }

    public void setDelayListener( ImageButton b )
    {
        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                Core.notify( new ToggleDelayCommand(), masterBus );
            }
        });
    }

    public void setDistortionListener( ImageButton b )
    {
        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                Core.notify( new ToggleDistortionCommand(), masterBus );
            }
        });
    }

    public void setFilterListener( ImageButton b )
    {
        b.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                Core.notify( new ToggleFilterCommand() );
            }
        });
    }

    @Override
    protected void onSizeChanged( int aWidth, int aHeight, int oldWidth, int oldHeight )
    {
        width  = aWidth;
        height = aHeight;
    }

    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent msg )
    {
        return _renderer.doKeyDown( keyCode, msg );
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp( int keyCode, KeyEvent msg )
    {
        return _renderer.doKeyUp( keyCode, msg );
    }

    @Override
    public boolean onTouchEvent( MotionEvent e )
    {
        return _renderer.handleTouchEvent( e );
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call / navigates to home page
     */
    @Override
    public void onWindowFocusChanged( boolean hasWindowFocus )
    {
        if ( !hasWindowFocus )
        {

        }
        else {

        }
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
    {
        if ( _renderer != null )
            _renderer.setSurfaceSize( width, height );
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated( SurfaceHolder holder )
    {
        createThreads();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed( SurfaceHolder holder )
    {
        _renderer.pause();
        _mwengine.pause();
    }

    public void onAccuracyChanged( Sensor arg0, int arg1 )
    {
        // nowt...
    }

    public final void onSensorChanged( SensorEvent event )
    {
        switch( event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:

                System.arraycopy( event.values, 0, gravityValues, 0, event.values.length );

                if ( geomagValues[ 0 ] != 0 )
                    ready = true;

                break;

                case Sensor.TYPE_MAGNETIC_FIELD:

                    System.arraycopy( event.values, 0, geomagValues, 0, event.values.length );

                    if ( gravityValues[ 2 ] != 0 )
                        ready = true;

                    break;

            // we currently don't listen for this sensor type as it's DEPRECATED
            // NOTE: azimuth = values[0], pitch = values[1] and roll = values[2] and
            // all immediately assignable to the respective class properties! just not for tablets...
//            case Sensor.TYPE_ORIENTATION:
//
//                System.arraycopy( event.values, 0, orientationValues, 0, event.values.length );
//                break;
        }

        if ( !ready )
            return;

        if ( SensorManager.getRotationMatrix( inR, null, gravityValues, geomagValues ))
        {
            // get the currently reported device rotation
            deviceRot = _display.getRotation();

            // this switch is necessary as mobile phones assume portrait as default orientation, which
            // suits the potrait-only mode of the application, HOWEVER: tablets are another story by assuming
            // the landscape orientation as the default
            switch ( deviceRot )
            {
                // portrait - normal - this seems to be the only report coming from mobiles!
                default:
                case Surface.ROTATION_0:
                    outR = inR;
                    //SensorManager.remapCoordinateSystem( inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR );
                    break;

                // rotated left (landscape - keys to bottom)
                case Surface.ROTATION_90:
                    SensorManager.remapCoordinateSystem( inR, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, outR );
                    break;

                // upside down
                case Surface.ROTATION_180:
                    SensorManager.remapCoordinateSystem( inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR );
                    break;

                // rotated right ( this is reported by Galaxy Tab 10.1 )
                case Surface.ROTATION_270:
                    SensorManager.remapCoordinateSystem( inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR );
                    tabletInverse = true; // see above
                    //SensorManager.remapCoordinateSystem( inR, SensorManager.AXIS_MINUS_Z, SensorManager.AXIS_X, outR );
                    break;
            }
            //DebugTool.log( "ROT > " + deviceRot + " rnd" + Math.random());

            // got a good rotation matrix
            SensorManager.getOrientation( outR, prefValues ); // using the transformed matrix above

            azimuth = ( float ) Math.toDegrees( prefValues[ 0 ]);

            // translate the azimuth to the format supplied by "TYPE_ORIENTATION"
            if ( azimuth < 0 )
                azimuth += 360.0f;

            pitch = ( float ) Math.toDegrees( prefValues[ 1 ]);

            // translate the pitch
            if ( pitch < -90 )
                pitch += ( -2 * ( 90 + pitch ));

            else if ( pitch > 90 )
                pitch += ( 2 * ( 90 - pitch ));

            // roll needs no scaling, just translation to degrees
            roll  = ( float ) -Math.toDegrees( prefValues[ 2 ]); // IGOR negative seems to work better (left/right otherwise mirrored?)

            if ( tabletInverse )
            {
                roll  = -roll;
                pitch = -pitch;
            }

            if ( masterBus == null ) // likely still starting
                return;

            if ( masterBus.delayActive )
                masterBus.delay.setFeedback(( float ) accelerometerScaler( pitch ));

            if ( _formantBus.formantActive )
                _formantBus.formant.setVowel( Math.round( accelerometerScaler( roll ) * 4d ));

            if ( _filterBus.filterActive )
                _filterBus.filter.setCutoff(( float ) accelerometerScaler( roll ) * AudioConstants.FILTER_MAX_FREQ );

            if ( masterBus.waveshaperActive )
                masterBus.waveShaper.setAmount(( float ) accelerometerScaler( azimuth ));
        }
    }

    public void createThreads()
    {
        // note the Object pooling

        _renderer = new ViewRenderer( getHolder(), this, getContext(), new Handler()
        {
            @Override
            public void handleMessage( Message m )
            {
                // Use for pushing back messages.
            }
        });

        _renderer.start();

        if ( _mwengine == null )
        {
            _mwengine = new MWEngine( getContext(), new StateObserver() );

            // note we only invoke the starting of the engine on actual creation, when restoring
            // threads (window focus change?) keep in mind the audio engine is Object pooled
            // (and will throw exceptions when setting a new song after restarting!)
            startAudioEngine( true );
        }
        else
        {
            if ( _mwengine.isPaused())
                _mwengine.start(); // necessary when returning from SongExport
        }
    }

    /**
     * invoke the render thread on the audio engine
     * @param outputCreation {boolean} whether we force creation of output, this is
     *                        automatically done when the renderer's sample rate and buffer size
     *                        don't match the calculated / saved settings
     */
    public void startAudioEngine( boolean outputCreation )
    {
        final Context context = getContext();
        final VOSetting savedQuality = _settingsProxy.getSetting( SettingsDefinitions.SETTING_BUFFER_SIZE );
        int bufferSize;

        if ( savedQuality != null )
        {
            bufferSize = Integer.parseInt( savedQuality.value );
        }
        else
        {
            // no saved buffer size == first run (with this native engine), calculate a nice default size
            // how dangerous is this exactly ??
            final int maxDesiredBufferSize = 2800;  // hopefully stopping at 2048 samples
            final int minDesiredBufferSize = 256;   // Samsung Nexus recommends 144, but is stable at 288...

            bufferSize = DevicePropertyCalculator.getRecommendedBufferSize(context);

            if ( bufferSize > maxDesiredBufferSize )
            {
                while ( bufferSize > maxDesiredBufferSize )
                    bufferSize /= 2;
            }
            if ( bufferSize < minDesiredBufferSize )
            {
                while ( bufferSize < minDesiredBufferSize )
                    bufferSize *= 2;
            }
            // save calculated buffer size in proxy
            (( SettingsProxy ) Core.retrieveProxy( SettingsProxy.NAME )).addOrUpdateSetting( SettingsDefinitions.SETTING_BUFFER_SIZE, bufferSize + "" );
        }
        KosmConstants.BUFFER_SIZE = bufferSize;
        final int sampleRate = DevicePropertyCalculator.getRecommendedSampleRate( context );

        // create new audio output
        if ( outputCreation || MWEngine.BUFFER_SIZE != bufferSize || MWEngine.SAMPLE_RATE != sampleRate )
            _mwengine.createOutput( sampleRate, KosmConstants.BUFFER_SIZE );

        _mwengine.getSequencerController().updateMeasures(1, getStepsPerBar()); // just one
        Core.notify(new CreateMasterBusCommand());

        // start / unpause thread (if thread was initialized and started previously)
        _mwengine.start();
        _mwengine.getSequencerController().setPlaying(true); // sequencer is always running in Kosm (otherwise no recording occurs!!)
    }
    
    /* suspending the app */
    
    public void pauseThreads()
    {
        DebugTool.log("Sequencer::pauseThreads()");

        _renderer.pause();
        _mwengine.pause();

        threadsActive = false;
    }

    // likely to be deprecated as we now use Object pooling and pause threads...
    public void destroyThreads()
    {
        DebugTool.log( "Sequencer::destroyThreads()" );

        _renderer.dispose();
        _mwengine.dispose();

        threadsActive = false;
    }

    private double accelerometerScaler( double aValue )
    {
        double theValue = Math.abs( MathTool.scale( aValue, 360, 1 )) * 2;

            if ( theValue > 1.0 )
                theValue = 1.0;

        return theValue;
    }

    private void deactivateParticleButtons()
    {
        ButtonTool.setImageButtonImage( buttonTwang, R.drawable.icon_ks );
        ButtonTool.setImageButtonImage( buttonSaw,   R.drawable.icon_saw );
        ButtonTool.setImageButtonImage( buttonSine,  R.drawable.icon_sine );
        ButtonTool.setImageButtonImage( buttonKick,  R.drawable.icon_kick );
        ButtonTool.setImageButtonImage( buttonSnare, R.drawable.icon_snare );
    }

    private void requestRecordingSave( boolean hadSpaceLeft )
    {
        final int theMessageId = hadSpaceLeft ? R.string.rec_req_save : R.string.rec_req_save_nospace;

        Confirm.confirm(getContext(), R.string.rec_stopped, theMessageId,
                // save requested
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // run the command in a thread to prevent blocking
                        // the UI thread under heavy load
                        new Thread(
                                new Runnable() {
                                    public void run() {
                                        Core.notify(new WriteRecordingCommand());
                                    }
                                }).start();
                    }
                },
                // no save requested > clear recording
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CacheWriter.flushCache();
                    }
                });
    }
}
