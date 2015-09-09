package nl.igorski.kosm.model.vo;

import nl.igorski.kosm.definitions.ParticleSounds;
import nl.igorski.lib.utils.ArrayTool;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 19-11-12
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
public final class ParticleEmitterVO
{
    public ParticleSounds particleSound;
    public float          radius;
    private long[]        _tapIntervals = new long[ 4 ];
    private int           _tapMeasures = 0;
    private long          _creation;
    private long          _lastTap = 0;

    /**
     * used for creation of new emitters
     *
     * @param aParticleSound {ParticleSounds} enum from ParticleSounds class, the sound to be
     *                       synthesized by each emitted particle
     * @param aRadius        {float} default radius to use for emitted particles
     * @param aCreationTime  {long} creation timestamp of this VO, this is the moment
     *                       the first tap of the sequence to be recorded received the touch DOWN event
     */
    public ParticleEmitterVO( ParticleSounds aParticleSound, float aRadius, long aCreationTime )
    {
        particleSound = aParticleSound;
        radius        = aRadius;
        _creation     = aCreationTime > 0 ? aCreationTime : System.currentTimeMillis();
    }

    /* public methods */

    /**
     * invoke upon each tap, returns true
     * when enough measures have been recorded
     * @param aTapTime {long} the System time in ms when the tap occurred
     * @return {boolean}
     */
    public boolean tap( final long aTapTime )
    {
        _lastTap = aTapTime;

        _tapIntervals[_tapMeasures] = aTapTime - _creation;
        return ( ++_tapMeasures == 4 );
    }

    /**
     * if the gap between tapped intervals is too large, this
     * emitters tap pattern has expired and a new one should be created
     * @param aTapTime {long} the System time in ms when the tap occurred
     * @return {boolean}
     */
    public boolean expired( final long aTapTime )
    {
        if ( _tapMeasures < 2 )
            return false;

        final long interval = aTapTime - _lastTap; // time between current and last tap

        return interval > 500 && ( interval > ( average() * 4 ));
    }

    public long average()
    {
        // we calculate the average of the last three taps
        // to prevent weirdness...

        final long[] intervals = new long[ 3 ];

        for ( int i = 1; i < _tapMeasures; ++i )
            intervals[ i - 1 ] = _tapIntervals[ i ];

        return ArrayTool.average( intervals );
    }
}
