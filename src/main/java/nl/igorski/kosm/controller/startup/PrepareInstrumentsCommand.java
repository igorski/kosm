package nl.igorski.kosm.controller.startup;

import nl.igorski.lib.audio.definitions.WaveForms;
import nl.igorski.lib.audio.factories.ProcessorFactory;
import nl.igorski.lib.audio.nativeaudio.SynthInstrument;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 25-01-15
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public class PrepareInstrumentsCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "PREPARE INSTRUMENTS COMMAND" );

        final Vector<InternalSynthInstrument> instruments = (( Vector<InternalSynthInstrument> ) aNote.getBody() );

        // shape sound for pad instrument

        final InternalSynthInstrument internalInstrument = instruments.get( 2 );
        final SynthInstrument instrument                 = internalInstrument.instrument;

        instrument.setVolume         ( .8f );
        instrument.setOctave         ( 4 );
        instrument.setWaveform       ( WaveForms.SQUARE_WAVE );
        instrument.setOsc2active     ( true );
        instrument.setOsc2waveform   ( WaveForms.SQUARE_WAVE );
        instrument.setOsc2octaveShift( 1 );
        instrument.setOsc2detune     ( 11 );
        instrument.setOsc2fineShift  ( 0 );
        instrument.getAdsr().setAttack ( 0.5199196f );
        instrument.getAdsr().setDecay  ( 0.9536901f );
        instrument.getAdsr().setSustain( 0f );
        instrument.getAdsr().setRelease( 0f );

        internalInstrument.processingChain.compressorActive = true;
        internalInstrument.processingChain.cAttack          = 20f;
        internalInstrument.processingChain.cRelease         = 500f;
        internalInstrument.processingChain.cRatio           = 0.86296266f;
        internalInstrument.processingChain.cGain            = 15f;
        internalInstrument.processingChain.cThreshold       = -23.754105f;

        ProcessorFactory.createCompressor( internalInstrument.processingChain );

        internalInstrument.processingChain.decimatorActive          = true;
        internalInstrument.processingChain.decimatorDistortion      = 16;
        internalInstrument.processingChain.decimatorDistortionLevel = 0.25f;

        ProcessorFactory.createDecimator( internalInstrument.processingChain );

        internalInstrument.processingChain.delayActive   = true;
        internalInstrument.processingChain.delayFeedback = 0.8697917f;
        internalInstrument.processingChain.delayMix      = 0.2520833f; // org is 0.5520833f
        internalInstrument.processingChain.delayTime     = 250f;

        ProcessorFactory.createDelay( internalInstrument.processingChain );

        internalInstrument.processingChain.formantActive = true;
        internalInstrument.processingChain.filterFormant = 0d;

        ProcessorFactory.createFormantFilter( internalInstrument.processingChain );

        internalInstrument.processingChain.bitCrusherActive = true;
        internalInstrument.processingChain.distortion       = 0.19809571f;
        internalInstrument.processingChain.distortionLevel  = 0.9380305f;

        ProcessorFactory.createBitCrusher( internalInstrument.processingChain );

        internalInstrument.processingChain.cacheActiveProcessors();

        super.execute( aNote ); // completes command
    }
}
