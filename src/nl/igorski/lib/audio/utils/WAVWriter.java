package nl.igorski.lib.audio.utils;

import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.storage.FileSystem;
import uk.labbookpages.wave.WavFile;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 27-06-12
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public final class WAVWriter
{
    /**
     * writes a mono audio buffer into a stereo WAVE file
     * 
     * @param aBuffer   {double[]}
     * @param aFileName {String}
     * 
     * @return {WavFile}
     */
    public static WavFile writeBufferToFile( double[] aBuffer, String aFileName )
    {
        WavFile output = null;

        try
        {
            final int sampleRate  = MWEngine.SAMPLE_RATE;
            final long numSamples = aBuffer.length;

            // create a stereo WAV file w/ the specified properties
            if ( !aFileName.contains( ".wav" ) && !aFileName.contains( "." ))
                aFileName = aFileName + ".wav";

            output = WavFile.newWavFile( FileSystem.open( aFileName ), 2, numSamples, 16, sampleRate );

            // create a local buffer
            final int bufferLength = 1024;
            double[][] localBuffer = new double[ 2 ][ bufferLength ];

            int frameCounter = 0;

            /* input buffer is (currently) a mono signal, we
               use the temporary local buffer to write the signal to
               two sides of the stereo field for stereo output */

            while ( frameCounter < numSamples )
            {
                final long remaining  = output.getFramesRemaining();
                final int writeAmount = ( remaining > bufferLength ) ? bufferLength : ( int ) remaining;

                // fill the stereo buffer
                for ( int s = 0; s < writeAmount; s++, frameCounter++ )
                {
                    final double theSample = aBuffer[ frameCounter ];

                    localBuffer[ 0 ][ s ] = theSample;
                    localBuffer[ 1 ][ s ] = theSample;
                }
                // write into file
                output.writeFrames( localBuffer, writeAmount );
            }
            // close the file
            output.close();
        }
        catch ( Exception e ) {}

        return output;
    }

    public static WavFile mergeWAVFiles( String[] aCachedFiles, String aFileName )
    {
        WavFile output = null;

        try
        {
            final int sampleRate  = MWEngine.SAMPLE_RATE;
            long numSamples       = 0;
            int numChannels       = 1;

            // calculate the amount of samples to write
            for ( final String cachedFile : aCachedFiles )
            {
                try
                {
                    WavFile f = WavFile.openWavFile( FileSystem.open( cachedFile ));
                    numSamples += f.getNumFrames();

                    if ( f.getNumChannels() > numChannels )
                        numChannels = f.getNumChannels();

                    f.close();
                }
                catch ( Exception gah ) {}
            }
            aFileName = formatWAVFileName( aFileName );
            output    = WavFile.newWavFile( FileSystem.open( aFileName ), numChannels, numSamples, 16, sampleRate );

            for ( final String theCacheFile : aCachedFiles )
            {
                try
                {
                    WavFile f = WavFile.openWavFile( FileSystem.open( theCacheFile ));
                    final int cacheBufferSize = ( int ) f.getNumFrames();

                    int[] buffer = new int[ cacheBufferSize ];
                    f.readFrames( buffer, 0, cacheBufferSize );

                    // we immediately clear the cache file so we have space on the device for writing the WAV sequence!
                    f.close();
                    FileSystem.delete( theCacheFile );

                    // write into merged output file
                    output.writeFrames( buffer, cacheBufferSize );

                }
                catch ( Exception gah ) {}
            }
            // close the file
            output.close();
        }
        catch ( Exception e )
        {
            DebugTool.log( "EXCEPTION " + e.getMessage() + " occurred during writing of recording" );
        }
        return output;
    }

    public static String formatWAVFileName( String aFileName )
    {
        // create a WAV file w/ the specified properties
        if ( !aFileName.contains( ".wav" )/* && !aFileName.contains( "." )*/ )
        {
            // no idea where that empty dot comes from...
            if ( aFileName.substring( aFileName.length() - 1 ).equals( "." ))
                aFileName = aFileName + "wav";
            else
                aFileName = aFileName + ".wav";
        }
        return aFileName;
    }
}
