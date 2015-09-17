package nl.igorski.kosm.view.ui;

import android.content.Context;
import android.os.Handler;
import android.text.TextPaint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import blissfulthinking.java.android.ape.AbstractParticle;
import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.controller.startup.PrepareAnimationAccessorsCommand;
import nl.igorski.kosm.definitions.Assets;
import nl.igorski.kosm.definitions.SequencerModes;
import nl.igorski.kosm.model.vo.ParticleEmitterVO;
import nl.igorski.kosm.view.ParticleSequencer;
import nl.igorski.kosm.view.physics.PhysicsWorld;
import nl.igorski.lib.utils.math.MathTool;
import blissfulthinking.java.android.ape.APEngine;
import blissfulthinking.java.android.apeforandroid.FP;
import nl.igorski.kosm.R;

import nl.igorski.kosm.controller.menu.CloseEffectsMenuCommand;
import nl.igorski.kosm.controller.menu.CloseWaveformMenuCommand;
import nl.igorski.lib.animation.Animator;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.ui.touch.TouchManager;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.threading.BaseThread;
import nl.igorski.kosm.view.physics.components.AudioParticle;

import java.util.Vector;

public final class ViewRenderer extends BaseThread
{
    /* touch event related */

    // we use class references to avoid unnecessary allocation and garbage collection of local vars

    private float _fingerX   = 0;
    private float _fingerY   = 0;
    private float _startX    = 0;
    private float _startY    = 0;
    private long  _downStart = 0;

    private float      oldFingerDistance = 1f;
    private float      newFingerDistance = 1f;
    private static int TOUCH_MODE_NONE   = 0;
    private static int TOUCH_MODE_ZOOM   = 1;
    private int        touchMode         = TOUCH_MODE_NONE;
    public int         sequencerMode     = SequencerModes.MODE_DEFAULT;

    /* container zoom related */

    private static float MAX_ZOOM = 1.5f;
    private static float MIN_ZOOM = .15f;

    /* physics related */

    private ParticleEmitterVO _emitterVO;
    private AbstractParticle _tappedObject = null;
    private PhysicsWorld     _physicsWorld = new PhysicsWorld();

    /* instructions */

    public Instructions instructions;
    public boolean      showInstructions = false;
    public int[]        modeInstructions = new int[]{ SequencerModes.MODE_360,
                                                      SequencerModes.MODE_EMITTER,
                                                      SequencerModes.MODE_HOLD,
                                                      SequencerModes.MODE_RESETTER };

    /* whether the surface has been created & is ready to draw */

    private SurfaceHolder     mSurfaceHolder;
    private ParticleSequencer container;
    private int               alpha = 0;

    public ViewRenderer( SurfaceHolder surfaceHolder, ParticleSequencer aContainer, Context context, Handler handler )
    {
        container        = aContainer;
        mSurfaceHolder   = surfaceHolder;

        final TextPaint paint = new TextPaint();
        paint.setTextSize( Assets.TEXT_SIZE_HEADER );
        paint.setColor   ( Color.WHITE );
        paint.setTypeface( Assets.DEFAULT_FONT );

        instructions = new Instructions( container.getWidth(), container.getHeight() );
        instructions.setContent( R.string.welcome_title, R.string.welcome_body );
        showInstructions = true;

        // prepare tween manager
        Core.notify( new PrepareAnimationAccessorsCommand() );

        init();
    }

    /* public methods */

    public void init()
    {
        synchronized ( mSurfaceHolder )
        {
            // initialize the physics world
            _physicsWorld.initWorld();
        }
    }

    @Override
    public void run()
    {
        Canvas c = null;

        while ( _isRunning )
        {
            try
            {
                c = mSurfaceHolder.lockCanvas( null );

                synchronized ( mSurfaceHolder )
                {
                    updateWorld();
                    render( c );
                }
            }
            catch ( Exception e )
            {
                // can occur when app loses focus...
            }
            finally
            {
                // if an exception is thrown during the above, we don't
                // leave the Surface in an inconsistent state

                if ( c != null )
                {
                    try {
                        mSurfaceHolder.unlockCanvasAndPost( c );
                    }
                    catch ( IllegalArgumentException e ) {
                        // java.lang.IllegalStateException: Surface has already been released
                    }
                }
            }
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

    /* callback invoked when the surface dimensions change. */

    public void setSurfaceSize( int width, int height )
    {
        // synchronized to make sure these all change atomically
        synchronized ( mSurfaceHolder )
        {
//                mCanvasWidth = width;
//                mCanvasHeight = height;
        }
    }

    public void switchMode( final int newMode )
    {
        sequencerMode = newMode;
        _emitterVO = null; // make sure we won't resume creation of a emitter
        _downStart = 0;

        // show instructions upon first use of mode ?
        showInstructions = false;

        for ( int i = 0, l = modeInstructions.length; i < l; ++i )
        {
            if ( modeInstructions[ i ] == sequencerMode)
            {
                showInstructions = true;
                modeInstructions[ i ] = -1;
                break;
            }
        }

        if ( showInstructions )
        {
            if ( Kosm.effectsMenuOpened )
                Core.notify( new CloseEffectsMenuCommand() );

            if ( Kosm.waveformMenuOpened )
                Core.notify( new CloseWaveformMenuCommand() );

            switch(sequencerMode)
            {
                case SequencerModes.MODE_HOLD:
                    instructions.setContent( R.string.hold_mode_title, R.string.hold_mode_body );
                    break;

                case SequencerModes.MODE_360:
                    instructions.setContent( R.string.three60_mode_title, R.string.three60_mode_body );
                    break;

                case SequencerModes.MODE_EMITTER:
                    instructions.setContent( R.string.emitter_mode_title, R.string.emitter_mode_body );
                    break;

                case SequencerModes.MODE_RESETTER:
                    instructions.setContent( R.string.resetter_mode_title, R.string.resetter_mode_body );
                    break;
            }
        }
    }

    public void destroy360container()
    {
        _physicsWorld.destroyContainer();
    }

    /* event handlers */

    /**
     * Handle a key-down event
     *
     * @param keyCode the key that was pressed
     * @param msg the original event object
     *
     * @return true
     */
    public final boolean doKeyDown( int keyCode, KeyEvent msg )
    {
        boolean handled = false;
        synchronized ( mSurfaceHolder )
        {
            switch ( keyCode )
            {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    handled = true;
                    break;
            }
            return handled;
        }
    }

    /**
     * handles a key-up event
     *
     * @param keyCode the key that was pressed
     * @param msg the original event object
     *
     * @return true if the key was handled and consumed, or else false
     */
    public final boolean doKeyUp( int keyCode, KeyEvent msg )
    {
        boolean handled = false;
        synchronized ( mSurfaceHolder )
        {
            switch ( keyCode )
            {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    handled = true;
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    handled = true;
                    break;
            }
            return handled;
        }
    }

    public final boolean handleTouchEvent( MotionEvent e )
    {
        final int action = e.getAction();
        _fingerX = e.getX();
        _fingerY = e.getY();

        switch ( action & MotionEvent.ACTION_MASK )
        {
            case MotionEvent.ACTION_DOWN:

                // if instructions were onscreen, hide them
                if ( showInstructions )
                {
                    showInstructions = false;
                    return true;
                }
                _downStart = System.currentTimeMillis();

                _startX = _fingerX;
                _startY = _fingerY;

                // check if we tapped on a particle
                Vector<AudioParticle> particles = container.getParticles();

                if ( sequencerMode != SequencerModes.MODE_RESETTER )
                {
                    synchronized ( particles )
                    {
                        for ( final AudioParticle s : particles )
                        {
                            final boolean clickedParticle = s.handleTouchDown(_fingerX, _fingerY);

                            if ( clickedParticle )
                            {
                                //DebugTool.log( "TAPPED ON PARTICLE" );
                                _tappedObject = s;
                                break;
                            }
                        }
                    }
                }
                alpha = 128;
                break;

            case MotionEvent.ACTION_UP:

                if ( e.getPointerCount() > 1 || touchMode == TOUCH_MODE_ZOOM )
                    return true;

                touchMode = TOUCH_MODE_NONE;

                // check if we didn't simply tap shortly on a particle for its removal
                if ( _tappedObject != null )
                {
                    //DebugTool.log( "DESTROY PARTICLE" );
                    (( AudioParticle ) _tappedObject ).destroy();
                    _tappedObject = null;
                }
                else
                {
                    // time between pointer down and up event is current time minus _downStart measurement
                    final long time    = System.currentTimeMillis();
                    final long elapsed = time - _downStart;

                    // create circular surface when in 360 mode
                    if (( sequencerMode == SequencerModes.MODE_360 ) && !_physicsWorld.hasContainer )
                    {
                        //_physicsWorld.createContainer(( int ) ( ParticleSequencer.width * .15 ), ( int ) _startX, ( int ) _startY );
                        _physicsWorld.createContainer(( int ) ( ParticleSequencer.width * .15 ),
                                                     ParticleSequencer.width / 2, ParticleSequencer.height / 2 );
                    }
                    // create emitter when in emitter mode
                    else if ( sequencerMode == SequencerModes.MODE_EMITTER )
                    {
                        if ( _downStart == 0 )
                            return true;

                        if ( _emitterVO == null || _emitterVO.expired( time ))
                            _emitterVO = new ParticleEmitterVO( container.particleSound, calcRadiusByTapInterval( elapsed ), _downStart );

                        final boolean create = _emitterVO.tap( time );
                        //DebugTool.log( "EMIT ? > " + create );

                        // enough taps recorded ? create emitter
                        if ( create )
                        {
                            //DebugTool.log( "CREATE FOR AVG => " + _emitterVO.average());

                            container.createEmitter(_emitterVO, _fingerX, _fingerY);
                            _emitterVO = null;
                        }
                    }
                    else if ( sequencerMode == SequencerModes.MODE_RESETTER )
                    {
                        if ( _downStart > 0 && elapsed > 1500l )
                        {
                            destroy360container();
                            ParticleSequencer.destroyEmitters();
                            ParticleSequencer.destroyFixed();
                        }
                    }
                    else
                    {
                        if ( _downStart == 0 )
                            return true;

                        // create new AudioParticle, its radius is
                        // calculated from the time difference between
                        // pointer down and up events
                        APEngine.addParticle( container.createAudioParticle( _fingerX + 20, _fingerY + 20,
                                              calcRadiusByTapInterval( elapsed ), createAsFixed() ));
                    }
                }
                alpha = 0;
                break;
            
            case MotionEvent.ACTION_POINTER_DOWN:

                oldFingerDistance = TouchManager.spacing( e );
                //DebugTool.log( "pinchDistance=" + oldFingerDistance );

                if ( oldFingerDistance > 10f )
                {
                    // we're now zooming!
                    touchMode = TOUCH_MODE_ZOOM;

                    _startX += ( oldFingerDistance / 2 );
                    _startY += ( oldFingerDistance / 2 );
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if ( touchMode == TOUCH_MODE_ZOOM )
                {
                    newFingerDistance = TouchManager.spacing( e );
                    //Log.d( "SYNTH", "newDist=" + newFingerDistance );
                    if ( newFingerDistance > 10f )
                    {
                        final float scale = newFingerDistance / oldFingerDistance;

                        int newRadius = ( int ) ( ParticleSequencer.width * MathTool.scale(scale, 8, MAX_ZOOM));

                        // prevent extreme size changes
                        final double maxIncrease = .045;

                        if ( newRadius > ( _physicsWorld.previousRadius * ( 1 + maxIncrease )))
                            newRadius = ( int ) ( _physicsWorld.previousRadius * ( 1 + maxIncrease ));

                        else if ( newRadius < ( _physicsWorld.previousRadius * ( 1 - maxIncrease )))
                            newRadius = ( int ) ( _physicsWorld.previousRadius * ( 1 - maxIncrease ));

                        //_physicsWorld.createContainer( newRadius, ( int ) _startX, ( int ) _startY );
                        if ( _physicsWorld.hasContainer )
                            _physicsWorld.createContainer( newRadius, ParticleSequencer.width / 2, ParticleSequencer.height / 2 );
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:

                touchMode = TOUCH_MODE_NONE;
                break;
        }
        return true;
    }

    /* private */

    private boolean createAsFixed()
    {
        return sequencerMode == SequencerModes.MODE_HOLD;
    }

    /**
     * draw all the particles onto the canvas
     * @param canvas {Canvas}
     */
    private void render( Canvas canvas )
    {
        if ( alpha == 0 )
        {
            canvas.drawARGB( 255, 0, 0, 0 ); // flood screen with black
        }
        else {
            // in fading background when touching
            if ( sequencerMode == SequencerModes.MODE_RESETTER )
                canvas.drawARGB( Math.abs( alpha - 128 ), 255, 0, 0 );
            else
                canvas.drawARGB( Math.abs( alpha - 128 ), 255, 255, 255 );
        }
        // APE physics engine
        _physicsWorld.draw(canvas);

        // instructions
        if ( showInstructions )
            instructions.draw( canvas );
    }

    private float calcRadiusByTapInterval( long aInterval )
    {
        // 1 seconds max ( equals largest available radius )
        if ( aInterval > 1000 )
            aInterval = 1000;

        float radius = ( float ) MathTool.scale( aInterval, 1000, AudioParticle.MAX_RADIUS );

        if ( radius < AudioParticle.MIN_RADIUS )
            radius = ( float ) AudioParticle.MIN_RADIUS;

        else if ( radius > AudioParticle.MAX_RADIUS )
            radius = ( float ) AudioParticle.MAX_RADIUS;

        return radius;
    }

    /*
     * Why use mLastTime, now and elapsed?
     * Well, because the frame rate isn't always constant, it could happen your normal frame rate is 25fps
     * then your char will walk at a steady pace, but when your frame rate drops to say 12fps, without elapsed
     * your character will only walk half as fast as at the 25fps frame rate. Elapsed lets you manage the slowdowns
     * and speedups!
     */
    private void updateWorld()
    {
        if ( alpha > 0 )
            --alpha;

        _physicsWorld.setGravity(FP.fromFloat((-container.roll) / 10), FP.fromFloat((-container.pitch) / 10));
        _physicsWorld.updateWorld();

        container.updateEmitters();

        try
        {
            Animator.update( System.currentTimeMillis() );
        }
        catch ( Exception e )
        {
            DebugTool.log("uh! oh! ViewRenderer error occurred when updating Animator...");
            DebugTool.logException( e );
            Animator.init(); // desperate fix, what's in the exception??
        }
    }
}
