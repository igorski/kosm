package nl.igorski.kosm.controller.effects;

import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.R;
import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.kosm.view.ParticleSequencer;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.ui.ButtonTool;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 25-01-15
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class TogglePitchshifterCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "TOGGLE PITCH SHIFTER COMMAND" );

        // apply pitch shfiter

        final MWProcessingChain pitchshifterChain = ParticleSequencer.masterBus;

        final boolean activated = !pitchshifterChain.pitchshifterActive;

        pitchshifterChain.pitchshifterActive = activated;
        pitchshifterChain.cacheActiveProcessors();

        final int buttonResourceId = activated ? R.drawable.icon_pitchshifter_active : R.drawable.icon_pitchshifter;
        ButtonTool.setImageButtonImage( Kosm.getBtnPitchshifter(), buttonResourceId );

        super.execute( aNote ); // completes command
    }
}
