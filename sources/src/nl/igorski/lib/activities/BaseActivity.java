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
package nl.igorski.lib.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 02-05-12
 * Time: 22:33
 * To change this template use File | Settings | File Templates.
 *
 * BaseActivity is the base class for nl.igorski.lib Activities
 * its constructor is invoked
 */
public class BaseActivity extends Activity
{
    protected boolean _initialized = false;

    protected static final int RESULT_CLOSE_ALL = -1;

    /* called when the activity is first created. */

    public void onCreate( Bundle savedInstanceState, int layoutResID, boolean fullScreen )
    {
        super.onCreate( savedInstanceState );

        Core.init( this ); // register Context in Core

        // full-screen mode requested ?
        if ( fullScreen )
        {
            requestWindowFeature( Window.FEATURE_NO_TITLE );
            getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN );
        }
        setContentView( layoutResID );

        // the application state can be sent via the key "STATE"
        // in the intents extras

        int applicationState = -1;
        final Intent theIntent = getIntent();

        if ( theIntent.getExtras() != null )
            applicationState = theIntent.getExtras().getInt( "STATE" );

        init( applicationState );
    }

    /* protected */

    protected void handleState( int aApplicationState )
    {
        // override in subclass
    }

    protected void initAssets()
    {
        // override in subclass
    }

    /**
     * make sure all activities belonging to this application
     * are closed, only invoke this method when quitting the
     * application manually
     */
    protected void destroyAllActivities()
    {
        setResult( RESULT_CLOSE_ALL );

        // makes sure we clear memory when reopening van app history!
        android.os.Process.killProcess( android.os.Process.myPid());
        //System.runFinalizersOnExit( true );
        //System.exit( 0 );

        destroy();
    }

    public void destroy()
    {
        finish();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        switch( resultCode )
        {
            // quick way to close all open activities
            case RESULT_CLOSE_ALL:
                setResult( RESULT_CLOSE_ALL );
                destroy();
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    /* private */

    private void init( int aApplicationState )
    {
        if ( !_initialized )
        {
            _initialized = true;

            if ( aApplicationState > -1 )
                handleState( aApplicationState );

            initAssets();
        }
    }
}
