package nl.igorski.lib.audio.definitions;

import nl.igorski.kosm.definitions.ParticleSounds;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 20-04-12
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public final class WaveForms
{
    public static final int SINE_WAVE       = 0;
    public static final int TRIANGLE        = 1;
    public static final int SAWTOOTH        = 2;
    public static final int SQUARE_WAVE     = 3;
    public static final int NOISE           = 4;
    public static final int PWM             = 5;
    public static final int KARPLUS_STRONG  = 6;

    /* public methods */

    public static int waveformByParticleSound( ParticleSounds aParticleSound )
    {
        switch( aParticleSound )
        {
            case PARTICLE_SINE:
                return WaveForms.SINE_WAVE;

            case PARTICLE_SAW:
                return WaveForms.SAWTOOTH;

            case PARTICLE_TWANG:
                return WaveForms.KARPLUS_STRONG;

            case PARTICLE_KICK:
                return PercussionTypes.KICK_808;

            case PARTICLE_PAD:
                return WaveForms.SQUARE_WAVE;
        }
        return WaveForms.KARPLUS_STRONG;
    }
}
