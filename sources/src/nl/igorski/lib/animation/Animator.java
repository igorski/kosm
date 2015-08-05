package nl.igorski.lib.animation;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 01-03-13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 *
 * AnimationManager acts as a wrapper for use with the
 * Aurelien Ribon Universal Tween Engine
 */
public final class Animator
{
    private static TweenManager _manager;
    private static boolean _isRendering;
    private static long    _lastRender;

    /* public methods */

    /**
     * creates a TweenManager to execute all animations, all tweens can be
     * created from everywhere in a application, as long as the tweens are executed
     * on the TweenManager in this class (use either getter for TweenManager of
     * pass Tweens directly to the "execute"-method)
     *
     * NOTE : "update"-method must be invoked periodically for actually executing
     * animations (either call "update" from main application render loop or start
     * a thread using the "startRenderCycle"-method
     *
     * @return {boolean}
     */
    public static boolean init()
    {
        if ( _manager != null )
            return false;

        _manager    = new TweenManager();
        _lastRender = System.currentTimeMillis();

        return true;
    }

    /**
     * register an accessor so the properties of a
     * class can be animated using the Tween Engine
     *
     * for instance: registerAccessor( Particle.class, new ParticleAccessor());
     *
     * @param aClass    {Class<?>} class whose properties should be tweenable
     * @param aAccessor {TweenAccessor<?>} accessor exposing the API for the aClass tweens
     */
    public static void registerAccessor( Class<?> aClass, TweenAccessor<?> aAccessor )
    {
        Tween.registerAccessor( aClass, aAccessor );
    }

    /**
     * starts a Tween in the TweenManager
     *
     * @param aTween {Tween} an UNSTARTED Tween
     * @return {Tween} tween after start has been invoked via the TweenManager
     */
    public static Tween execute( Tween aTween )
    {
        try
        {
            return aTween.start( _manager );
        }
        catch ( Exception e ) {}

        return null;
    }

    /**
     * kills all running Tweens
     */
    public static void killAll()
    {
        try
        {
            _manager.killAll();
        }
        catch ( Exception e ) {}
    }

    /**
     * kills all animations belonging to the given target
     *
     * @param aObject
     */
    public static void killTweensOf( Object aObject )
    {
        try
        {
            _manager.killTarget( aObject );
        }
        catch ( Exception e ) {}
    }

    public static void killTweenByType( Object aObject, int aTweenType )
    {
        try
        {
            _manager.killTarget( aObject, aTweenType );
        }
        catch ( Exception e ) {}
    }

    /**
     * returns a reference to the TweenManager so starting Tweens can
     * be managed outside of this class
     *
     * @return {TweenManager}
     */
    public static TweenManager getTweenManager()
    {
        return _manager;
    }

    /**
     * in case of an application with a dedicated render loop, call this update
     * method on each cycle to update the TweenManager (proceeds to next step of
     * all registered Tweens)
     *
     * @param timeStamp {long} timestamp of the new step position
     */
    public static void update( long timeStamp )
    {
        // update by calculating the elapsed time since the last render ( in seconds )
        try
        {
            _manager.update(( timeStamp - _lastRender ) / 1000f );
        }
        catch ( Exception e )
        {
            killAll();  // likely something we want to solve!
        }
        _lastRender = timeStamp;
    }

    /**
     * in case of no main render cycle within the application, the
     * Tweening engine can be updated from a thread running on the
     * main UI thread
     *
     * @param aPreferredFrameRate {int} preferred frame rate
     */
    public static void startRenderCycle( final int aPreferredFrameRate )
    {
        _isRendering = true;

        new Thread( new Runnable()
        {
            private long lastMillis = -1;

            public void run()
            {
                while ( _isRendering )
                {
                    if ( lastMillis > 0 )
                    {
                        final long currentMillis = System.currentTimeMillis();
                        final float delta        = ( currentMillis - lastMillis ) / 1000f;

                        Core.getActivity().runOnUiThread( new Runnable()
                        {
                            public void run()
                            {
                                _manager.update( delta );
                            }
                        });
                        lastMillis = currentMillis;
                    }
                    else {
                        lastMillis = System.currentTimeMillis();
                    }
                    try {
                        Thread.sleep( 1000 / aPreferredFrameRate );
                    }
                    catch( InterruptedException ex ) { }
                }
            }
        }).start();
    }

    /**
     * stops the render thread
     */
    public static void stopRenderCycle()
    {
        _isRendering = false;
    }
}
