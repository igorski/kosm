package nl.igorski.kosm.controller.effects;

import nl.igorski.kosm.R;
import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.lib.audio.factories.ProcessorFactory;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.ui.ButtonTool;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 25-01-15
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class ToggleDistortionCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "TOGGLE DISTORTION COMMAND" );

        final MWProcessingChain processingChain = (( MWProcessingChain ) aNote.getBody() );
        final boolean activated = !processingChain.waveshaperActive;
        
        if ( !processingChain.waveshaperActive )
        {
            processingChain.waveShaper = ProcessorFactory.createWaveShaper( processingChain );
        }
        processingChain.waveshaperActive = activated;
        processingChain.cacheActiveProcessors();

        final int buttonResourceId = activated ? R.drawable.icon_distortion_active : R.drawable.icon_distortion;

        ButtonTool.setImageButtonImage( Kosm.btnDistortion, buttonResourceId );

        super.execute( aNote ); // completes command
    }
}
