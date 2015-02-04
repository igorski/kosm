package nl.igorski.kosm.util;

import nl.igorski.lib.audio.nativeaudio.BaseAudioEvent;
import nl.igorski.lib.utils.time.Timeout;

import java.util.Vector;

/**
 * EventDisposalUtil is basically the last step to reference
 * native layer BaseAudioEvents before the garbage collector should
 * pick them up (e.g. break all other references when checking them in here!!)
 *
 * there are two queues, the disposal of items in the "stale" and "fresh" queue is
 * always the given timeout in milliseconds apart, this effectively gives the
 * engine x amount of milliseconds time to finish "working with" the BaseAudioEvents
 * as a poor mans way of overcoming events being deleted when their contents are still queried!!
 */
public final class EventDisposalUtil
{
    private static Vector<BaseAudioEvent> fresh = new Vector<BaseAudioEvent>(); // last to be cleared
    private static Vector<BaseAudioEvent> stale = new Vector<BaseAudioEvent>(); // first to be cleared

    private final static int TIMEOUT   = 5000;
    private static boolean _hasTimeout = false;

    public static void disposeEvent( BaseAudioEvent event )
    {
        fresh.add( event );

        event.destroy();

        checkDisposability();
    }

    private static void checkDisposability()
    {
        if (( stale.size() > 0 || fresh.size() > 0 ) && !_hasTimeout )
            delayedDisposal();
    }

    private static void delayedDisposal()
    {
        _hasTimeout = true;

        Timeout.setTimeout( TIMEOUT, new Runnable()
        {
            @Override
            public void run()
            {
                stale.clear();

                if ( fresh.size() > 0 ) {
                    stale = ( Vector ) fresh.clone();
                    fresh.clear();
                }
                _hasTimeout = false;
                checkDisposability();
            }

        }, true );
    }
}
