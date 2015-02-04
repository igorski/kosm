package nl.igorski.kosm.controller.effects;

import nl.igorski.kosm.R;
import nl.igorski.kosm.activities.Kosm;
import nl.igorski.kosm.audio.MWProcessingChain;
import nl.igorski.lib.audio.definitions.AudioConstants;
import nl.igorski.lib.audio.factories.ProcessorFactory;
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
public class ToggleFilterCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "TOGGLE FILTER COMMAND" );

        final MWProcessingChain processingChain = (( MWProcessingChain ) aNote.getBody() );

        final boolean activated = !processingChain.formantActive;

//        final RouteableOscillator lfo = _instrumentProperties.instrument.getROsc();

        // filter
/*
        if ( !processingChain.filterActive )
        {
            // create LFO
//            lfo.setWave( WaveForms.SAWTOOTH );
//            lfo.setSpeed( .5f );
//            lfo.setDestination( OscillatorDestinations.FILTER );

            processingChain.filterCutoff    = AudioConstants.FILTER_MAX_FREQ * .3f;
            processingChain.filterResonance = AudioConstants.FILTER_MAX_RESONANCE * .5f;
            processingChain.filter          = ProcessorFactory.createFilter(processingChain);//, lfo );

//            processingChain.filter.setLFO( lfo.getLinkedOscillator() );
        }
        else
        {
//            lfo.setDestination( OscillatorDestinations.NONE );
            processingChain.filter.setLFO( null );
        }
        processingChain.filterActive = activated;
*/
        // formant filter

        if ( !processingChain.formantActive ) {
            processingChain.formant = ProcessorFactory.createFormantFilter( processingChain );
        }

        processingChain.formantActive = activated;
        processingChain.cacheActiveProcessors();

        final int buttonResourceId = activated ? R.drawable.icon_filter_active : R.drawable.icon_filter;
        ButtonTool.setImageButtonImage( Kosm.btnFilter, buttonResourceId );

        super.execute( aNote ); // completes command
    }
}
