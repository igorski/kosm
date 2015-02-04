package nl.igorski.lib.audio.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-09-12
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public final class BufferCloner
{
    public static double[][] cloneRecording( double[][] aRecording )
    {
        final double[][] output = new double[ aRecording.length ][];

        int writePointer = 0;

        for ( final double[] buffer : aRecording )
        {
            output[ writePointer ] = new double[ buffer.length ];

            System.arraycopy( buffer, 0, output[ writePointer ], 0, buffer.length );
            ++writePointer;
        }
        return output;
    }
}
