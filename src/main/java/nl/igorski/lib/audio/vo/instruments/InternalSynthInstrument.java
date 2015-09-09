package nl.igorski.lib.audio.vo.instruments;

import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.audio.interfaces.IUpdateableInstrument;
import nl.igorski.lib.audio.factories.ProcessorFactory;

import nl.igorski.lib.audio.nativeaudio.Arpeggiator;
import nl.igorski.lib.audio.nativeaudio.BufferUtility;
import nl.igorski.lib.audio.nativeaudio.SynthInstrument;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 22-04-12
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public final class InternalSynthInstrument
{
    public SynthInstrument     instrument;
    public MWProcessingChain processingChain;
    public int                 index;

    /* private variables */

    private IUpdateableInstrument _listener;

    public InternalSynthInstrument( int aIndex, IUpdateableInstrument aListener )
    {
        index           = aIndex;
        _listener       = aListener;

        // will auto register this instrument in the native layer
        instrument      = new SynthInstrument();
        processingChain = new MWProcessingChain( instrument.getAudioChannel().getProcessingChain() );

        final Arpeggiator arp = instrument.getArpeggiator();

        arp.setAmountOfSteps( 4 );
        arp.setShiftForStep ( 0, 12 );
        arp.setShiftForStep ( 1, -12 );
        arp.setShiftForStep ( 2, 12 );
        arp.setShiftForStep ( 3, -12);
        arp.setStepSize     ( BufferUtility.calculateSamplesPerBeatDivision( MWEngine.SAMPLE_RATE,
                              120.0, 16 ));
    }

    /* public methods */

    public void createEffectsChain()
    {
        // auto-constructs all available effects (these are still disabled until activated)

        ProcessorFactory.createBitCrusher   ( processingChain );
        //ProcessorFactory.createCompressor   ( processingChain );
        ProcessorFactory.createDecimator    ( processingChain );
        ProcessorFactory.createDelay        ( processingChain );
        ProcessorFactory.createFilter       ( processingChain );
        ProcessorFactory.createFM           ( this );
        ProcessorFactory.createFormantFilter( processingChain );
        ProcessorFactory.createPhaser       ( processingChain );
        ProcessorFactory.createPitchShifter ( processingChain );

        // scale down the bit crushers input mix for the synth instrument
        processingChain.bitCrusher.setInputMix( .65f );
    }

    public void reset()
    {
        processingChain.reset();
        instrument.setArpeggiatorActive( false );
    }

    public void setWaveForm( int aWaveForm, boolean aDoBroadcast )
    {
        instrument.setWaveform( aWaveForm );

        if ( aDoBroadcast )
            broadcastUpdate();
    }

    public void setWaveForm( int aWaveForm )
    {
        setWaveForm( aWaveForm, true ); // auto broadcasts
    }

    public void setOSC2Waveform( int aWaveForm, boolean aDoBroadcast )
    {
        instrument.setOsc2waveform(aWaveForm);

        if ( aDoBroadcast )
            broadcastUpdate();
    }

    public void sestOS2CWaveform( int aWaveForm )
    {
        setOSC2Waveform( aWaveForm, true ); // auto broadcasts
    }

    public void setEnvelopes( float aAttack, float aDecay, float aVolume, boolean aDoBroadcast )
    {
        instrument.getAdsr().setAttack( aAttack );
        instrument.getAdsr().setDecay ( aDecay );
        instrument.setVolume ( aVolume );

        if ( aDoBroadcast )
            broadcastUpdate();
    }

    public void setEnvelopes( float aAttack, float aDecay, float aVolume )
    {
        setEnvelopes( aAttack, aDecay, aVolume, true ); // auto broadcasts
    }

    public void setOSC2Active( boolean aActive )
    {
        instrument.setOsc2active( aActive );

        // if ( aDoBroadcast )
            broadcastUpdate();
    }

    public void setOSC2Tuning( double aOctave, double aFineShift, float aDetune, boolean aDoBroadcast )
    {
        instrument.setOsc2octaveShift(( int ) aOctave );
        instrument.setOsc2fineShift  (( int ) aFineShift );
        instrument.setOsc2detune     ( aDetune );

        if ( aDoBroadcast )
            broadcastUpdate();
    }

    public void setListener( IUpdateableInstrument listener )
    {
        _listener = listener;
    }

    /* private methods */

    private void broadcastUpdate()
    {
        if ( _listener != null )
            _listener.updateInstrument( this );
    }
}
