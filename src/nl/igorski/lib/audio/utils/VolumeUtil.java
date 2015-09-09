package nl.igorski.lib.audio.utils;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 10/12/12
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public final class VolumeUtil
{
    private static final double	FACTOR1 = 20.0 / Math.log( 10.0 );
    private static final double	FACTOR2 = 1 / 20.0;

    public static double lin2log( double dLinear )
    {
        return FACTOR1 * Math.log( dLinear );
    }

    public static double log2lin( double dLogarithmic )
    {
        return Math.pow( 10.0, dLogarithmic * FACTOR2 );
    }
}
