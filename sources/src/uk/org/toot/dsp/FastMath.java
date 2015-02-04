package uk.org.toot.dsp;

import static java.lang.Math.PI;

public class FastMath
{
    // http://www.devmaster.net/forums/showthread.php?t=5784
    private static final float S_B = (float)(4 / PI);
    private static final float S_C = (float)(-4 / (PI*PI));
    // -PI < x < PI
    public final static float sin(final float x) {
        return S_B * x + S_C * x * abs(x);
    }

    private static final float TWODIVPI = (float)(2 / PI);
    // -PI < x < PI
    // thanks scoofy[AT]inf[DOT]elte[DOT]hu
    // for musicdsp.org pseudo-code improvement
    public final static float triangle(float x) {
        x += PI;			// 0 < x < 2*PI
        x *= TWODIVPI;   	// 0 < x < 4
        x -= 1;				// -1 < x < 3
        if ( x > 1 ) x -= 4f;
        return abs(-(abs(x)-2)) - 1;
    }

    // provide a faster abs, no NaN handling
    public final static float abs(final float x) {
        return x < 0 ? -x : x;
    }

    // provide a faster min, no NaN handling
    public final static float min(final float a, final float b) {
        return a < b ? a : b;
    }

    // provide a faster max, no NaN handling
    public final static float max(final float a, final float b) {
        return a > b ? a : b;
    }

    // http://martin.ankerl.com/2009/01/05/approximation-of-sqrtx-in-java/
    // average error of 1.57%, maximum error 4.02%
    public final static double sqrt(final double a) {
        return Double.longBitsToDouble(
                ((Double.doubleToLongBits(a) >> 32) + 1072632448) << 31);
    }

    // http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/
    // usually within an error margin of 5% to 12%, in extreme cases sometimes up to 25%
    public final static double pow(final double a, final double b) {
        final int x = (int) (Double.doubleToLongBits(a) >> 32);
        final int y = (int) (b * (x - 1072632447) + 1072632447);
        return Double.longBitsToDouble(((long) y) << 32);
    }

    //http://martin.ankerl.com/2007/02/11/optimized-exponential-functions-for-java/
    public final static double exp(final double val) {
        final long tmp = (long) (1512775 * val) + 1072632447;
        return Double.longBitsToDouble(tmp << 32);
    }

    //http://martin.ankerl.com/2007/02/11/optimized-exponential-functions-for-java/
    public final static double ln(final double val) {
        final double x = Double.doubleToLongBits(val) >> 32;
        return (x - 1072632447) / 1512775;
    }

    /**
     * pade-approximation to tanh.
     * @param x input
     * @return tanh of input
     */
    public final static float tanh(float x) {
        // clamping is advisable because |x| > 5 does cause very high output
        // we clamp at 3 because that's where output is unity and C1/C2 continuous
        if ( x < -3 ) return -1;
        if ( x > 3 ) return 1;
        final float x2 = x * x;
        return x * (27 + x2) / (27 + 9 * x2);
    }
}
