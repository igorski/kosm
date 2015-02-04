package nl.igorski.lib.utils.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 26-06-12
 * Time: 22:11
 * To change this template use File | Settings | File Templates.
 */
public final class NetworkUtil
{
    /**
     * quick query to check if the device has
     * currently got Internet access
     * @param aContext {Context} current application context
     * @return {boolean}
     */
    public static boolean hasInternet( Context aContext )
    {
        ConnectivityManager cm =
            ( ConnectivityManager ) aContext.getSystemService( Context.CONNECTIVITY_SERVICE );

        return cm.getActiveNetworkInfo() != null &&
           cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * open a URL in the device's browser, note you can
     * only request this from a Activity
     *
     * @param aContext {Context} current application context
     * @param aURL {String} URL to open
     */
    public static void openURL( Context aContext, String aURL )
    {
        Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( aURL ));
        aContext.startActivity( browserIntent );
    }
}
