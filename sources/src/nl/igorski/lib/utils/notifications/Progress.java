package nl.igorski.lib.utils.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 08-07-12
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 *
 * convenience class to create / show / update a Progress Dialog
 * to be used across threads / async tasks by invoking it on
 * the main UI thread
 */
public final class Progress
{
    private static ProgressDialog _pd;

    /* public */

    public static void initDialog( final Context aContext, final String aTitle, final String aMessage )
    {
        Core.getActivity().runOnUiThread(
        new Runnable()
        {
            public void run()
            {
                _pd = ProgressDialog.show( aContext, aTitle, aMessage, true, false );
            }
        });
    }

    public static void initDialog( Context aContext, int aTitle, int aMessage )
    {
        initDialog( aContext, aContext.getString( aTitle ), aContext.getString( aMessage ));
    }

    public static void updateDialogText( String aTitle, String aMessage )
    {
        if ( _pd != null )
        {
            final ProgressDialog pd = _pd;

            pd.setTitle  ( aTitle );
            pd.setMessage( aMessage );
        }
        else
        {
            initDialog( Core.getContext(), aTitle, aMessage );
        }
    }

    public static void updateDialogText( int aTitle, int aMessage )
    {
        final Context theContext = _pd.getContext();

        updateDialogText( theContext.getString( aTitle ), theContext.getString( aMessage ));
    }

    public static void dismiss()
    {
        if ( _pd != null )
            _pd.dismiss();
    }
}
