package nl.igorski.lib.audio.definitions;

import nl.igorski.lib.audio.MWEngine;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 22-04-12
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 *
 * constants used by the engine, as the variables
 * are static you can override these should you desire
  */
public final class AudioConstants
{
    /* octave range */

    public static int MIN_OCTAVE                = 1;
    public static int MAX_OCTAVE                = 8;

    /* filter frequency ranges */

    // TODO: make constants in C++ classes!!

    public static float FILTER_MIN_FREQ        = 50.0f;
    public static float FILTER_MAX_FREQ        = MWEngine.SAMPLE_RATE / 4;
    public static float FILTER_MIN_RESONANCE   = 0.1f;
    public static float FILTER_MAX_RESONANCE   = ( float )( Math.sqrt( 2 ) * .5 );

    /* phaser frequency range */

    public static float PHASER_MIN_FREQ        = 440.0f;
    public static float PHASER_MAX_FREQ        = 1600.0f;

    /* compressor ranges */

    public static float MIN_COMPRESSION_RATIO   = 0.01f;
    public static float MAX_COMPRESSION_RATIO   = 1.75f;

    /* user interface related */

    public static final int ROTARY_UPDATE_DELAY = 500; // milliseconds
}
