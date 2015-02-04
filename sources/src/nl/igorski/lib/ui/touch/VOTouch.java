package nl.igorski.lib.ui.touch;

import nl.igorski.lib.ui.interfaces.ITouch;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-08-12
 * Time: 18:43
 *
 * VOTouch is the base Value Object for a touch event, you
 * can extend this class to add custom properties for
 * custom use, this Object provides enough properties and
 * methods to be of value in the TouchManager
 */
public class VOTouch implements ITouch
{
    protected int pointer_id   = 0;

    protected int xPos         = 0;

    protected int yPos         = 0;

    public VOTouch(int aPointerId, int aXPos, int aYPos)
    {
        pointer_id = aPointerId;
        xPos       = aXPos;
        yPos       = aYPos;
    }

    /* public */

    public void setPointerId( int aPointerId )
    {
        pointer_id = aPointerId;
    }

    public int getPointerId()
    {
        return pointer_id;
    }

    public void updatePosition( int x, int y )
    {
        xPos = x;
        yPos = y;
    }

    public int getX()
    {
        return xPos;
    }

    public int getY()
    {
        return yPos;
    }
}
