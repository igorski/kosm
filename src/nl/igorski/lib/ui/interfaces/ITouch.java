package nl.igorski.lib.ui.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-08-12
 * Time: 19:21
 *
 * the base methods for a ITouch implementation
 * to be used with the TouchManager
 */
public interface ITouch
{
    public void setPointerId( int aPointerId );

    public int getPointerId();

    public void updatePosition( int x, int y );

    public int getX();

    public int getY();
}
