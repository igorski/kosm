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
package nl.igorski.lib.utils.threading;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/12/12
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 *
 * BaseThread provides convenience operations for manipulating
 * thread execution and temporarily halting it
 */
public class BaseThread extends Thread implements Runnable
{
    protected boolean _isRunning = false;

    // used when suspending the application

    protected final Object _pauseLock;
    protected boolean      _paused;

    /* don't forget to invoke these in your subclass! */

    public BaseThread()
    {
        _pauseLock = new Object();
        _paused    = false;
    }

    @Override
    public void start()
    {
        if ( !_isRunning )
            super.start();
        else
            unpause();

        _isRunning = true;
    }

    /**
     * invoke when the application suspends, this should
     * halt the execution of the run method and cause the
     * thread to clean up to free CPU resources
     */
    public void pause()
    {
        synchronized ( _pauseLock )
        {
            _paused = true;
        }
    }

    /**
     * invoke when the application regains focus
     */
    public void unpause()
    {
        synchronized ( _pauseLock )
        {
            _paused = false;
            _pauseLock.notifyAll();
        }
    }

    public boolean isPaused()
    {
        return _paused;
    }

    /**
     * when cleaning up the thread when
     * it isn't needed anymore
     */
    public void dispose()
    {
        _isRunning = false;
    }

    /**
     * the run method should look like this ( don't invoke
     * the super call ! ) all code should be inside the
     * while statement, this allows for cleanup when
     * using the "destroy"-method
     */
    @Override
    public void run()
    {
        while ( _isRunning )
        {
            // custom code here...

            // used for suspending the threads, implement
            // this in the your override if you intend to
            // (un)pause this thread

            synchronized ( _pauseLock )
            {
                while ( _paused )
                {
                    try
                    {
                        _pauseLock.wait();
                    }
                    catch ( InterruptedException e ) {}
                }
            }
        }
    }
}
