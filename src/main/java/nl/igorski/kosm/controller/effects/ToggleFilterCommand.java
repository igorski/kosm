package nl.igorski.kosm.controller.effects;

import nl.igorski.kosm.Kosm;
import nl.igorski.kosm.model.KosmInstruments;
import nl.igorski.lib.audio.definitions.AudioConstants;
import nl.igorski.lib.audio.definitions.OscillatorDestinations;
import nl.igorski.lib.audio.definitions.WaveForms;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;

import nl.igorski.kosm.R;

import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.kosm.definitions.ParticleSounds;
import nl.igorski.lib.audio.factories.ProcessorFactory;
import nl.igorski.mwengine.core.RouteableOscillator;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
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

        // apply LFO-operated high pass filter

        final InternalSynthInstrument filterInstrument  = KosmInstruments.getInstrumentByParticleSound( ParticleSounds.PARTICLE_SINE );

        final MWProcessingChain filterChain = filterInstrument.processingChain;

        final boolean activated = !filterChain.filterActive; // whether the command will activate or deactivate the filter

        final RouteableOscillator lfo = filterInstrument.instrument.getROsc();

        // filter

        if ( activated )
        {
            // create LFO
            lfo.setWave( WaveForms.SAWTOOTH );
            lfo.setSpeed( .5f );
            lfo.setDestination( OscillatorDestinations.FILTER );

            filterChain.filterCutoff    = AudioConstants.FILTER_MAX_FREQ * .3f;
            filterChain.filterResonance = AudioConstants.FILTER_MAX_RESONANCE * .5f;
            filterChain.filter          = ProcessorFactory.createFilter( filterChain, lfo );

            if ( !filterChain.filter.hasLFO())
                filterChain.filter.setLFO( lfo.getLinkedOscillator() );
        }
        else
        {
            lfo.setDestination( OscillatorDestinations.NONE );
        }
        filterChain.filterActive = activated;
        filterChain.cacheActiveProcessors();

        final int buttonResourceId = activated ? R.drawable.icon_filter_active : R.drawable.icon_filter;
        ButtonTool.setImageButtonImage( Kosm.getBtnFilter(), buttonResourceId );

        super.execute( aNote ); // completes command
    }
}
