package nl.igorski.kosm.controller.effects;

import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.R;
import nl.igorski.kosm.definitions.ParticleSounds;
import nl.igorski.kosm.model.KosmInstruments;
import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.lib.audio.factories.ProcessorFactory;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
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
public class ToggleFormantCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "TOGGLE FORMANT COMMAND" );

        // apply a formant filter

        final InternalSynthInstrument formantInstrument = KosmInstruments.getInstrumentByParticleSound( ParticleSounds.PARTICLE_KICK );

        final MWProcessingChain formantChain = formantInstrument.processingChain;

        final boolean activated = !formantChain.formantActive; // whether the command will activate or deactivate the filter

        // formant filter

        if ( activated ) {
            formantChain.formant = ProcessorFactory.createFormantFilter( formantChain );
        }
        formantChain.formantActive = activated;
        formantChain.cacheActiveProcessors();

        final int buttonResourceId = activated ? R.drawable.icon_formant_active : R.drawable.icon_formant;
        ButtonTool.setImageButtonImage( Kosm.getBtnFormant(), buttonResourceId );

        super.execute( aNote ); // completes command
    }
}
