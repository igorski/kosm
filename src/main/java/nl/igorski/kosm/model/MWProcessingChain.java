package nl.igorski.kosm.model;

import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.audio.nativeaudio.*;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 08-03-14
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public final class MWProcessingChain
{
    // to overcome the processors destructors being invoked by SWIG
    // when these references pass out of scope, we hold a reference to
    // each processor created in a session, as the native audio engine
    // uses Object pooling these are never destroyed until the application quits

    /* FM properties */

    public boolean fmActive;
    public FrequencyModulator fm;

    /* filter properties */

    public float filterCutoff;
    public float filterResonance;
    public boolean filterActive;
    public Filter filter;

    /* formant filter properties */

    public double filterFormant;
    public boolean formantActive;
    public FormantFilter formant;

    /* phaser properties */

    public float phaserRate;
    public float phaserFeedback;
    public float phaserDepth;
    public boolean phaserActive;
    public Phaser phaser;

    /* distortion properties */

    public float distortion;
    public float distortionLevel;
    public boolean waveshaperActive;
    public WaveShaper waveShaper;

    public boolean bitCrusherActive;
    public BitCrusher bitCrusher;

    public float decimatorDistortion;
    public float decimatorDistortionLevel;
    public boolean decimatorActive;
    public Decimator decimator;

    /* delay properties */

    public float delayTime;
    public float delayMix;
    public float delayFeedback;
    public boolean delayActive;
    public Delay delay;

    /* compressor properties */

    public float cAttack;
    public float cRelease;
    public float cThreshold;
    public float cGain;
    public float cRatio;
    public boolean compressorActive;
    public Compressor compressor;

    /* pitchshifter properties */

    public PitchShifter pitchShifter;

    /* LPF / HPF filter properties */

    public float lpfCutoff;
    public float hpfCutoff;
    public boolean lpfHpfActive;
    public LPFHPFilter lpfhpf;

    /* Finalizers */

    public Finalizer finalizer;
    public boolean finalizerActive;
    
    private ProcessingChain _chain;

    public MWProcessingChain( ProcessingChain aNativeProcessingChain )
    {
        _chain = aNativeProcessingChain;
        
        filterCutoff    = MWEngine.SAMPLE_RATE / 4;
        filterResonance = ( float )( Math.sqrt( 1 ) / 2 );
        filterFormant   = 0;

        phaserDepth     = 0.5f;
        phaserFeedback  = 0.7f;
        phaserRate      = 0.5f;

        delayTime       = 250f;
        delayMix        = .25f;
        delayFeedback   = .5f;

        distortion               = .5f;
        distortionLevel          = .5f;
        decimatorDistortion      = 16f;
        decimatorDistortionLevel = .25f;

        cAttack         = 20;
        cRelease        = 500;
        cThreshold      = 8;
        cGain           = 15.0f;
        cRatio          = 1.2f;

        lpfCutoff = MWEngine.SAMPLE_RATE;
        hpfCutoff = 5;
    }

    public void cacheActiveProcessors()
    {
        _chain.reset();

        /* processors */

        // always active (but mostly idle sparing CPU sources)
        if ( pitchShifter != null )
            _chain.addProcessor( pitchShifter );

        if ( fmActive )
            _chain.addProcessor( fm );

        if ( waveshaperActive )
            _chain.addProcessor( waveShaper );

        if ( bitCrusherActive )
            _chain.addProcessor( bitCrusher );

        if ( phaserActive )
            _chain.addProcessor( phaser );

        if ( filterActive )
            _chain.addProcessor( filter );

        if ( formantActive )
            _chain.addProcessor( formant );

        if ( decimatorActive )
            _chain.addProcessor( decimator );

        if ( delayActive )
            _chain.addProcessor( delay );

        if ( compressorActive )
            _chain.addProcessor( compressor );

        if ( finalizerActive )
            _chain.addProcessor( finalizer );

        if ( lpfHpfActive )
            _chain.addProcessor( lpfhpf );
    }

    public void reset()
    {
        fmActive         = false;
        waveshaperActive = false;
        bitCrusherActive = false;
        decimatorActive  = false;
        phaserActive     = false;
        filterActive     = false;
        formantActive    = false;
        compressorActive = false;
        delayActive      = false;
        lpfHpfActive     = false;
        finalizerActive  = false;

        cacheActiveProcessors();
    }
}
