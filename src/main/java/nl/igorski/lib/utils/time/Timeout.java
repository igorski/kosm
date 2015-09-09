/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 Igor Zinken - http://www.igorski.nl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package nl.igorski.lib.utils.time;

import android.os.Handler;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.utils.time.interfaces.ITimeoutHandler;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-05-12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 *
 * Timeout provides an interface similar to JavaScript setTimeout and
 * clearTimeout where delayed operations can be registered and cancelled
 * if their execution isn't desired any more
 */
public final class Timeout
{
    private static HashMap<Runnable, Handler> _timeouts;

    /**
     * a way to execute a method after a given timeout, by holding a reference
     * to the Runnable aMethod that should be executed, it is also possible to
     * cancel the timeout using the clearTimeout-method
     *
     * @param aDelay      {int} delay in milliseconds
     * @param aMethod     {Runnable} method to execute after timeout, with
     *                               callback logic in public "run"-method
     * @param aIsThreaded {boolean} set to true when this is invoked within a thread
     */
    public static void setTimeout( final int aDelay, final Runnable aMethod, boolean aIsThreaded )
    {
        if ( _timeouts == null )
            _timeouts = new HashMap<Runnable, Handler>();

        // in case the timeout will be cleared, this second callback
        // ensures the timeouts HashMap will remove the registered handlers

        final Runnable proxiedCallback = new Runnable()
        {
            public void run()
            {
                _timeouts.remove( aMethod );
            }
        };

        if ( !aIsThreaded )
        {
            final Handler handler = new Handler();
            _timeouts.put( aMethod, handler );

            handler.postDelayed( aMethod,         aDelay );
            handler.postDelayed( proxiedCallback, aDelay );
        }
        else
        {
            Core.getActivity().runOnUiThread( new Runnable()
            {
                public void run()
                {
                    final Handler handler = new Handler();
                    _timeouts.put( aMethod, handler );

                    handler.postDelayed( aMethod,         aDelay );
                    handler.postDelayed( proxiedCallback, aDelay );
                }
            });
        }
    }

    /**
     * kill a pending timeout to prevent it's callback
     * from firing
     *
     * @param {Runnable} aMethod the method we have registered
     *                   to be executed after a timeout
     * @return {boolean}
     */
    public static boolean clearTimeout( Runnable aMethod )
    {
        if ( _timeouts != null )
        {
            final Handler handler = _timeouts.get( aMethod );

            if ( handler != null )
            {
                handler.removeCallbacks( aMethod );
                _timeouts.remove( aMethod );

                return true;
            }
        }
        return false;
    }

    /**
     * execute a method after a given timeout using a custom handler, this
     * is executed in a new thread
     *
     * NOTE : this is not canceable (...yet?)
     *
     * @param aDelay   {int}             delay in milliseconds
     * @param aHandler {ITimeoutHandler} callback to execute when timeout expires
     */
    public static void setTimeout( final int aDelay, final ITimeoutHandler aHandler )
    {
        new Thread( new Runnable()
        {
            private boolean doRun = true;

            public void run()
            {
                while ( doRun )
                {
                    try
                    {
                        Thread.sleep( aDelay );
                    }
                    catch ( InterruptedException e ) {}

                    doRun = false;
                    aHandler.onComplete();
                }
            }
        }).start();
    }
}
