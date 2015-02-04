package nl.igorski.lib.ui.touch;

import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import nl.igorski.lib.ui.interfaces.ITouch;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-08-12
 * Time: 18:45
 *
 * TouchManager is a helper class to store references
 * to pointers for managed multi-touch use, it also
 * provides methods for multi pointer calculations
 */
public final class TouchManager
{
    // max 5 (initial touch and four extra pointers)
    private ITouch[] _touches;

    public TouchManager()
    {
        _touches = new ITouch[ 5 ];
    }

    /* public */

    public boolean hasTouches()
    {
        int amount = 0;

        for ( int i = 0, l = _touches.length; i < l; ++i )
        {
            if ( _touches[ i ] != null )
                ++amount;
        }
        return amount > 0;
    }

    public ITouch[] getAllTouches()
    {
        return _touches;
    }

    public void removeAllTouches()
    {
        for ( int i = 0, l = _touches.length; i < l; ++i )
            _touches[ i ] = null;
    }

    /**
     * retrieve the VOTouch Object by the pointer ID
     * if it didn't exist, it is created
     *
     * @param aPointerId {int}
     * @param aX {int} pointer X position, used on creation
     * @param aY {int} pointer Y position, used on creation
     * @param aTouchClass {Class} optional ITouch implementation to instantiate when the touch
     *        didn't exist yet (this overrides the default creation of a igorski.lib...VOTouch Object)
     *
     * @return {ITouch}
     */
    public ITouch getTouchByPoint( int aPointerId, int aX, int aY, ITouch aTouchClass )
    {
        ITouch touch = getTouchByPoint( aPointerId );

        if ( touch == null && aPointerId < _touches.length )
        {
            touch = aTouchClass;
            touch.setPointerId( aPointerId );
            touch.updatePosition( aX, aY );

            _touches[ aPointerId ] = touch;
        }
        return touch;
    }

    public ITouch getTouchByPoint( int aPointerId, int aX, int aY )
    {
        ITouch touch = getTouchByPoint( aPointerId );

        if ( touch == null && aPointerId < _touches.length )
        {
            touch = new VOTouch( aPointerId, aX, aY );
            _touches[ aPointerId ] = touch;
        }
        return touch;
    }

    public ITouch getTouchByPoint( int aPointerId )
    {
        if ( aPointerId >= _touches.length )
            return null;

        if ( _touches[ aPointerId ] != null )
            return _touches[ aPointerId ];

        return null;
    }

    public void removeTouchByPoint( int aPointerId )
    {
        if ( aPointerId >= _touches.length )
            return;

        if ( _touches[ aPointerId ] != null )
            _touches[ aPointerId ] = null;
    }

    /* static helper methods */

    /**
     * calculate the distance between two pointers
     * @param event {MotionEvent}
     * return {float}
     */
    public static final float spacing( MotionEvent event )
    {
        final float x = event.getX( 0 ) - event.getX( 1 );
        final float y = event.getY( 0 ) - event.getY( 1 );

        return FloatMath.sqrt( x * x + y * y );
    }

    /**
     * calculate the point in the middle of two pointers
     * @param point {Point} to avoid garbage collections that can cause noticeable
     *        pauses in the application, we reuse an existing object to store the
     *        result rather than allocating and returning a new one each time.
     *
     * @param event {MotionEvent}
     */
    public static final void midPoint( PointF point, MotionEvent event )
    {
        final float x = event.getX( 0 ) + event.getX( 1 );
        final float y = event.getY( 0 ) + event.getY( 1 );

        point.set( x / 2, y / 2 );
    }
}
