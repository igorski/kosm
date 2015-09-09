package nl.igorski.lib.utils.math;

import nl.igorski.lib.utils.TypeConverter;

/**
 * Created by IntelliJ IDEA.
 * User  igorzinken
 * Date  4/12/12
 * Time  1 01 PM
 * To change this template use File | Settings | File Templates.
 */
public final class MathTool
{
    /*
     * @method rand
     * returns a random number within a given range
     */
    public static int rand( int low, int high )
    {
        return TypeConverter.longToInt (Math.round( Math.random() * ( high - low )) + low );
    }

    /**
     * @method scale
     * scales a value against a scale different to the one "value" stems from
     * i.e. a UI slider with a value 0 - 100 ( percent ) to match against a
     * scale with a maximum value of 255
     *
     * @param value           {double} value to get scaled to
     * @param maxValue        {double} the maximum value we are likely to expect for param value
     * @param maxCompareValue {double} the maximum value in the scale we're matching against
     *
     * @return double
     */
    public static double scale( double value, double maxValue, double maxCompareValue )
    {
        double ratio = maxCompareValue / maxValue;
        return value * ratio;
    }

    /**
     * @method deg2rad
     * translates a value in degrees to radians
     *
     * @param deg {float} value in degrees
     * @eturn {float}
     */
    public static float deg2rad( float deg )
    {
        return deg / ( 180f / ( float ) Math.PI );
    }

    /**
     * @method rad2deg
     * translates a value in radians to degrees
     *
     * @param rad {float} value in radians
     * @return {float}
     */
    public static float rad2deg( float rad )
    {
        return rad / ( ( float ) Math.PI / 180f );
    }

    /**
     *  faster alternatives to base Math methods
     *  abs and max are faster as there is no NaN handling
     */

    // provide a faster abs, no NaN handling

    public static double abs( final double x )
    {
        return x < 0 ? -x : x;
    }

    // provide a faster max, no NaN handling

    public static double max( final double a, final double b )
    {
        return a > b ? a : b;
    }
}
