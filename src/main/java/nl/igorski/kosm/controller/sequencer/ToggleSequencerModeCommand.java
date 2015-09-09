package nl.igorski.kosm.controller.sequencer;

import android.widget.Button;
import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.definitions.SequencerModes;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;

import nl.igorski.kosm.R;

import nl.igorski.kosm.view.ui.ViewRenderer;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-01-15
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public final class ToggleSequencerModeCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "TOGGLE SEQUENCER MODE COMMAND" );

        final Button buttonModeToggle = (( Button ) aNote.getBody() );
        final ViewRenderer ui         = Kosm.getViewRenderer();

        final int currentMode   = ui.mode;
        final int amountOfModes = SequencerModes.MODES.length;
        int newMode             = currentMode;

        for ( int i = 0; i < amountOfModes; ++i )
        {
            newMode = SequencerModes.MODES[ i ];

            if ( newMode == currentMode )
            {
                final int nextMode = ( i + 1 == amountOfModes ) ? 0 : i + 1;
                newMode = SequencerModes.MODES[ nextMode ];
                break;
            }
        }

        if ( currentMode != newMode )
        {
            if ( newMode == SequencerModes.MODE_DEFAULT ||
                 newMode == SequencerModes.MODE_HOLD    ||
                 newMode == SequencerModes.MODE_EMITTER )
            {
                // make sure 360 containers are destroyed (NOTE: emitters and fixed particles are allowed!)
                ui.destroy360container();
            }
        }
        ui.switchMode(newMode);

        switch ( ui.mode )
        {
            case SequencerModes.MODE_DEFAULT:
                buttonModeToggle.setText( R.string.mode_default );
                break;

            case SequencerModes.MODE_HOLD:
                buttonModeToggle.setText( R.string.mode_hold );
                break;

            case SequencerModes.MODE_EMITTER:
                buttonModeToggle.setText( R.string.mode_emitter );
                break;

            case SequencerModes.MODE_360:
                buttonModeToggle.setText( R.string.mode_360 );
                break;

            case SequencerModes.MODE_RESETTER:
                buttonModeToggle.setText( R.string.mode_resetter );
                break;
        }
        super.execute( aNote ); // completes command
    }
}
