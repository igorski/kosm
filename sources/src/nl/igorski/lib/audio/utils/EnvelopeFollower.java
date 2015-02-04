package nl.igorski.lib.audio.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 5/25/12
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnvelopeFollower
{
    public double envelope;

    protected double _attack;

    protected double _release;

    public EnvelopeFollower( double maxGain, double attackMs, double releaseMs, int sampleRate )
    {
        envelope = 0.0;

        _attack  = Math.pow( 0.01, maxGain / ( attackMs  * sampleRate * 0.001 ));
        _release = Math.pow( 0.01, maxGain / ( releaseMs * sampleRate * 0.001 ));
    }

    /* public */

    public void process( double src, int skip )
    {
        final double v = Math.abs( src );
        src += skip;

        if ( v > envelope )
            envelope = _attack * ( envelope - v ) + v;
        else
            envelope = _release * ( envelope - v ) + v;
    }
}
