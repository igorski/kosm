package nl.igorski.lib.utils.notifications;

import android.content.Context;
import android.widget.Toast;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 5/10/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 *
 * Alert shows quick feedback messages to the user, it is basically
 * a wrapper for the native "Toast" method and can be called from
 * inner threads outside of the main UI thread
 */
public final class Alert
{
    /**
     * show a quick feedback message
     * @param aContext {Context} the current context
     * @param aMessage {String} the message to show
     */
    public static void show( Context aContext, String aMessage )
    {
        doShow( aContext, aMessage );
    }

    /**
     * show a quick feedback message from a String resource
     * @param aContext {Context} the current contex
     * @param aMessage {int} res ID of the String message
     */
    public static void show( Context aContext, int aMessage )
    {
        doShow( aContext, aContext.getString( aMessage ));
    }

    /* private */

    private static void doShow( final Context aContext, final String aMessage )
    {
        Core.getActivity().runOnUiThread(
        new Runnable()
        {
            public void run()
            {
                Toast.makeText( aContext, aMessage, Toast.LENGTH_LONG ).show();
            }
        });
    }
}
