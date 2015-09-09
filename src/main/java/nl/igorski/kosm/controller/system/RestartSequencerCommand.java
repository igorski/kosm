package nl.igorski.kosm.controller.system;

import android.app.Activity;
import android.content.Intent;
import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.model.SettingsProxy;
import nl.igorski.kosm.model.vo.VOSetting;
import nl.igorski.lib.activities.BaseActivity;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.utils.notifications.Alert;
import nl.igorski.kosm.R;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 14-06-14
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class RestartSequencerCommand extends BaseSimpleCommand
{
    // to make sure we don't endlessly repeat restart requests
    // (indicates an engine start "panic") we impose a threshold

    private final String TIME_OUT_IDENTIFIER = "RESTART";
    private final int MAX_TIMEOUT            = 5000; // in milliseconds

    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log("RESTART SEQUENCER COMMAND");

        final Activity mainActivity = Core.getActivity();

        if ( mainActivity != null )
        {
            final SettingsProxy proxy = ( SettingsProxy )( Core.retrieveProxy( SettingsProxy.NAME ));

            // to overcome endlessly looping through restarts, we check whether
            // the previous requested restart (if there was one) occurred before
            // above mentioned timeout...

            if ( proxy != null )
            {
                final VOSetting cachedSetting = proxy.getSetting( TIME_OUT_IDENTIFIER );
                final long currentTime        = System.currentTimeMillis();

                if ( cachedSetting != null )
                {
                    final long lastResetTimeout = Long.parseLong( cachedSetting.value );
                    final long elapsedTime      = currentTime - lastResetTimeout;

                    if ( elapsedTime < MAX_TIMEOUT )
                    {
                        Alert.show( Core.getContext(), R.string.error_no_opensl );
                        return;
                    }
                }
                // save a reference to this last restart request
                proxy.addOrUpdateSetting( TIME_OUT_IDENTIFIER, currentTime + "" );
            }
            // close current Activity and request a new SongEditor Activity

            Intent myIntent = new Intent( mainActivity.getBaseContext(), Kosm.class );
            ((BaseActivity) ( mainActivity )).destroy();

            mainActivity.startActivityForResult( myIntent, 0 );
        }
        super.execute( aNote ); // completes command
    }
}
