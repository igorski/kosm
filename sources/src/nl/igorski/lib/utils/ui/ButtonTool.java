package nl.igorski.lib.utils.ui;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.ImageButton;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 25-09-12
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public final class ButtonTool
{
    /**
     * change the image source for a ImageButton
     *
     * @param aButton     {ImageButton} the ImageButton
     * @param aDrawableId {int} resource ID of the desired drawable resource
     */
    public static void setImageButtonImage( ImageButton aButton, int aDrawableId )
    {
        aButton.setImageDrawable( getResources().getDrawable( aDrawableId ));
    }

    /**
     * change the background for a Button
     *
     * @param aButton     {Button} the Button
     * @param aDrawableId {int} resource ID of the desired drawable resource
     */
    public static void setButtonBackground( Button aButton, int aDrawableId )
    {
        aButton.setCompoundDrawablesWithIntrinsicBounds( null, getResources().getDrawable( aDrawableId ), null, null );
    }

    /* private */

    private static Resources getResources()
    {
        return Core.getContext().getResources();
    }
}
