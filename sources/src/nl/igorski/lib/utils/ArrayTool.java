package nl.igorski.lib.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/16/12
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public final class ArrayTool
{
    /* reverse array order */

    public static int[] reverse( int[] data )
    {
        for ( int left = 0, right = data.length - 1; left < right; left++, right-- )
        {
            // swap the values at the left and right indices
            int temp      = data[ left ];
            data[ left ]  = data[ right ];
            data[ right ] = temp;
        }
        return data;
    }

    public static double[] reverse( double[] data )
    {
        for ( int left = 0, right = data.length - 1; left < right; left++, right-- )
        {
            // swap the values at the left and right indices
            double temp   = data[ left ];
            data[ left ]  = data[ right ];
            data[ right ] = temp;
        }
        return data;
    }

    public static String[] reverse( String[] data )
    {
        for ( int left = 0, right = data.length - 1; left < right; left++, right-- )
        {
            // swap the values at the left and right indices
            String temp   = data[ left ];
            data[ left ]  = data[ right ];
            data[ right ] = temp;
        }
        return data;
    }

    /* push an item */

    public static int[] push( int[] array, int pushValue )
    {
        int[] longer = new int[ array.length + 1 ];
        System.arraycopy( array, 0, longer, 0, array.length );
        longer[ array.length ] = pushValue;

        return longer;
    }

    /* push an item only if it's value didn't exist in the Array */

    public static int[] pushUnique( int[] array, int pushValue )
    {
        for ( final int i : array )
        {
            if ( i == pushValue )
                return array;
        }
        int[] longer = new int[ array.length + 1 ];
        System.arraycopy( array, 0, longer, 0, array.length );
        longer[ array.length ] = pushValue;

        return longer;
    }

    /**
     * @param array {int[]} the array to work on
     * @param key   {int} the value of the array key
     * @return {int[]} array without the given key
     */
    public static int[] splice( int[] array, int key )
    {
        int[] shorter = new int[ array.length - 1 ];

        for ( int i = 0, l = array.length, w = 0; i < l; ++i )
        {
            if ( array[ i ] != key )
            {
                shorter[ w ] = array[ i ];
                ++w;
            }
        }
        return shorter;
    }

    public static long average( long[] array )
    {
        long result = 0;

         for ( final long i : array )
           result += i;

        return result / array.length;
    }
}
