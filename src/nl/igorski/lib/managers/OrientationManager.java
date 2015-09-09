package nl.igorski.lib.managers;

import android.content.res.Configuration;
import android.view.Display;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 9/3/12
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public final class OrientationManager
{
    public static int getScreenOrientation( Display getOrient )
    {
        int orientation;

        if ( getOrient.getWidth() == getOrient.getHeight())
        {
            orientation = Configuration.ORIENTATION_SQUARE;
        }
        else
        {
            if ( getOrient.getWidth() < getOrient.getHeight())
                orientation = Configuration.ORIENTATION_PORTRAIT;
            else
                orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }
}
