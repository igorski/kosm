package nl.igorski.lib.audio.factories;

import nl.igorski.kosm.definitions.KosmConstants;
import nl.igorski.kosm.model.MWProcessingChain;
import nl.igorski.mwengine.MWEngine;
import nl.igorski.lib.audio.definitions.AudioConstants;
import nl.igorski.lib.audio.definitions.OscillatorDestinations;
import nl.igorski.mwengine.core.*;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 5/1/12
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 *
 * Factory used to open I(Bus)Processors, all new instances
 * created throughout a application should use this class
 * as the sole creation method to ensure all properties are
 * always initialized in the same manner
 */
public final class ProcessorFactory
{
    public static int OUTPUT_CHANNELS = 1;  // must match global.h !!

    public static Delay createDelay( MWProcessingChain chain )
    {
        Delay d;

        if ( chain.delay == null )
        {
            d = new Delay(
                chain.delayTime,
                KosmConstants.MAX_DELAY_TIME,
                chain.delayMix,
                chain.delayFeedback, OUTPUT_CHANNELS
            );
            chain.delay = d;
        }
        else
        {
            d = chain.delay;

            d.setDelayTime( chain.delayTime );
            d.setMix      ( chain.delayMix );
            d.setFeedback ( chain.delayFeedback );
        }
        return d;
    }

    public static Filter createFilter( MWProcessingChain chain )
    {
        Filter f;

        if ( chain.filter == null )
        {
            f = new Filter(
                chain.filterCutoff,
                chain.filterResonance,
                AudioConstants.FILTER_MIN_FREQ,
                AudioConstants.FILTER_MAX_FREQ,
                0.0f, OUTPUT_CHANNELS
            );
            chain.filter = f;
        }
        else
        {
            f = chain.filter;

            f.setCutoff   ( chain.filterCutoff );
            f.setResonance( chain.filterResonance );
        }
        return f;
    }

    public static WaveShaper createWaveShaper( MWProcessingChain chain )
    {
        WaveShaper w;

        if ( chain.waveShaper == null )
        {
            w = new WaveShaper( chain.distortion, chain.distortionLevel );
            chain.waveShaper = w;
        }
        else
        {
            w = chain.waveShaper;

            w.setAmount( chain.distortion );
            w.setLevel ( chain.distortionLevel );
        }
        return w;
    }

    public static BitCrusher createBitCrusher( MWProcessingChain chain )
    {
        BitCrusher b;

        if ( chain.bitCrusher == null )
        {
            b = new BitCrusher( chain.distortion, 1f, chain.distortionLevel );
            chain.bitCrusher = b;
        }
        else
        {
            b = chain.bitCrusher;

            b.setAmount   ( chain.distortion );
            b.setOutputMix( chain.distortionLevel );
        }
        return b;
    }

    public static Decimator createDecimator( MWProcessingChain chain )
    {
        Decimator d;

        if ( chain.decimator == null )
        {
            d = new Decimator(( int ) chain.decimatorDistortion, chain.decimatorDistortionLevel );
            chain.decimator = d;
        }
        else
        {
            d = chain.decimator;

            d.setBits(( int ) chain.decimatorDistortion );
            d.setRate( chain.decimatorDistortionLevel );
        }
        return d;
    }

    public static FormantFilter createFormantFilter( MWProcessingChain chain )
    {
        FormantFilter ff;

        if ( chain.formant == null )
        {
            ff = new FormantFilter( chain.filterFormant );
            chain.formant = ff;
        }
        else
        {
            ff = chain.formant;
            ff.setVowel( chain.filterFormant );
        }
        return ff;
    }

    public static Compressor createCompressor( MWProcessingChain chain )
    {
        Compressor c;

        if ( chain.compressor == null ) {
            c = new Compressor();
            chain.compressor = c;
        } else {
            c = chain.compressor;
        }
        c.setThreshold( chain.cThreshold );
        c.setAttack   ( chain.cAttack );
        c.setRelease  ( chain.cRelease );
        c.setRatio    ( chain.cRatio );
        //c.setGain     ( chain.getCGain());

        return c;
    }

    public static PitchShifter createPitchShifter( MWProcessingChain chain )
    {
        PitchShifter p;

        if ( chain.pitchShifter == null )
        {
            p = new PitchShifter( 1.0f, 4 );
            chain.pitchShifter = p;
        }
        else {
            p = chain.pitchShifter;
        }
        return p;
    }

    public static LPFHPFilter createLPFHPFilter( MWProcessingChain chain )
    {
        LPFHPFilter f;

        if ( chain.lpfhpf == null )
        {
            f = new LPFHPFilter(( float ) MWEngine.SAMPLE_RATE, 55, OUTPUT_CHANNELS );
            chain.lpfhpf = f;
        }
        else {
            f = chain.lpfhpf;
        }
        return f;
    }

    public static Finalizer createFinalizer( MWProcessingChain chain )
    {
        Finalizer f;

        if ( chain.finalizer == null )
        {
            f = new Finalizer( 2f, 500f, MWEngine.SAMPLE_RATE, OUTPUT_CHANNELS );
            chain.finalizer = f;
        }
        else {
            f = chain.finalizer;
        }
        return f;
    }

    /* InternalSynthInstrument specific */

    public static Filter createFilter( MWProcessingChain chain, RouteableOscillator oscillator )
    {
        Filter filter = createFilter( chain );

        // in case we already had a filter, and it is the current instruments destination
        // for the secondary LFO, we assign the current LFO speed to the new Filter instance
        // NOTE : not all chains have instantiated oscillators, so do a null check!!

        if ( oscillator != null &&
             oscillator.getDestination() != OscillatorDestinations.NONE &&
             oscillator.getDestination() == OscillatorDestinations.FILTER )
        {
            filter.setLFORate( oscillator.getSpeed() );
        }
        return filter;
    }
}
