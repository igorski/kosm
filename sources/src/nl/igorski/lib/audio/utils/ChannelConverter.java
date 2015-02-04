package nl.igorski.lib.audio.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 07-01-13
 * Time: 00:23
 * To change this template use File | Settings | File Templates.
 */
public final class ChannelConverter
{
    public static double[] convertBufferToMono( double[][] aInputBuffer )
    {
        final double channelAmount = aInputBuffer.length;

        if ( channelAmount == 1 )
            return aInputBuffer[ 0 ];

        final double amp = 1 / channelAmount;

        double[] output = new double[ aInputBuffer[ 0 ].length ];

        // fill with silence

        for ( int i = 0, l = output.length; i < l; ++i )
            output[ i ] = 0.0;

        // sum channels to mono

        for ( int i = 0, l = output.length; i < l; ++i )
        {
            for ( int c = 0; c < channelAmount; ++c )
            {
                output[ i ] += aInputBuffer[ c ][ i ] * amp;
            }
        }
        return output;
    }
}
