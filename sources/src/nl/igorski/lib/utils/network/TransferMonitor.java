package nl.igorski.lib.utils.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import nl.igorski.lib.utils.network.interfaces.ITransfer;
import nl.igorski.lib.utils.notifications.Progress;
import org.apache.http.HttpResponse;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
public final class TransferMonitor
{
    /**
     * request a window stating a file transfer is in progress
     *
     * @param aContext  {Context} current context
     * @param aTransfer {ITransfer} ITransfer Object performing the request
     * @param aTitle    {String} title to show in the progress window
     * @param aBody     {String} body text to show in the progress window
     */
    public static void monitoredTransfer( Context aContext, final ITransfer aTransfer, String aTitle, String aBody )
    {
        new MonitorTask( aContext, aTransfer, aTitle, aBody ).execute();
    }

    public static void monitoredTransfer( Context aContext, ITransfer aTransfer, int aTitle, int aBody )
    {
        monitoredTransfer( aContext, aTransfer, aContext.getString( aTitle ), aContext.getString( aBody ));
    }

    /* private */

    private final static class MonitorTask extends AsyncTask<URL, Integer, Long>
    {
        private ITransfer _transfer;

        public MonitorTask( Context aContext, ITransfer transfer, String aTitle, String aBody )
        {
            _transfer = transfer;

            Progress.initDialog(aContext, aTitle, aBody);
        }

        protected Long doInBackground( URL... urls )
        {
            HttpResponse response = null;

            try
            {
                response = _transfer.doTransfer();
            }
            catch( Exception e )
            {
                Log.d( "SYNTH", "EXCEPTION DURING OCCURRED DURING TRANSFER => " + e.getLocalizedMessage());
            }

            Progress.dismiss();
            _transfer.returnHandler( response );

            return ( long ) 0;
        }

        protected void onProgressUpdate( Integer... progress )
        {
            Log.d( "SYNTH", " PROGRESS => " + progress[ 0 ]);
        }

        protected void onPostExecute( Long l )
        {
            Log.d( "SYNTH", "KLAAR MET TASK" );
        }
    }
}
