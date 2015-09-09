package nl.igorski.kosm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.time.Timeout;
import nl.igorski.lib.utils.time.interfaces.ITimeoutHandler;
import nl.igorski.lib.activities.BaseActivity;

public final class Bootstrap extends BaseActivity
{
    private final int APP_START_DELAY = 5000; // in milliseconds

    private boolean _appStarted = false;

    /* called when the activity is first created. */

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState, R.layout.bootstrap, true );

        DebugTool.LOG_TAG = "KOSM";
    }

    /* event handlers */

    private final class handleStartClicked implements View.OnClickListener
    {
        public void onClick( View aView )
        {
            startApplication();
        }
    }

    /* protected */

    @Override
    protected void initAssets()
    {
        final ImageView logo = ( ImageView ) findViewById( R.id.ButtonStartApp );
        logo.setOnClickListener( new handleStartClicked());

        Timeout.setTimeout( APP_START_DELAY, new ITimeoutHandler()
        {
            public void onComplete()
            {
                startApplication();
            }
        });
    }

    /* private */

    private void startApplication()
    {
        // start Kosm
        if ( !_appStarted )
        {
            Intent myIntent = new Intent( getBaseContext(), Kosm.class );
            myIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivityForResult( myIntent, 0 );
            _appStarted = true;
        }
        destroy(); // close this Activity
    }
}
