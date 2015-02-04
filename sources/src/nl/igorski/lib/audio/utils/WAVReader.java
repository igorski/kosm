package nl.igorski.lib.audio.utils;

import uk.labbookpages.wave.WavFile;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 06-01-13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public final class WAVReader
{
    public static double[][] readAudioBufferFromWAV( String aWaveFilePath )
    {
        WavFile wavFile;

        // Open the wav file specified as the first argument
        try
        {
            wavFile = WavFile.openWavFile( new File( aWaveFilePath ));
        }
        catch ( Exception e )
        {
            return null;
        }
        return readAudioBufferFromWAVFile( wavFile );
    }

    public static double[][] readAudioBufferFromWAVFile( WavFile wavFile )
    {
        // Display information about the wav file
        //wavFile.display();

        // Get the number of audio channels in the wav file
        int numChannels = wavFile.getNumChannels();

         // Create a buffer of 100 frames
        double[][] output = new double[ numChannels ][ ( int ) wavFile.getNumFrames() ];
        double[] buffer   = new double[ 100 * numChannels ];
        int framesRead = 1, writePointer = 0;

        do
        {
            // read frames into buffer
            try
            {
                framesRead = wavFile.readFrames( buffer, 100 );
            }
            catch ( Exception e ) {}

            // loop through frames and collect each available channels buffer
            // within its own key in the output buffer Array

            for ( int s = 0; s < framesRead * numChannels; s++ )
            {
                output[ writePointer % numChannels ][ writePointer ] = buffer[ s ];

                if ( s % numChannels == numChannels - 1 )
                    ++writePointer;
            }
         }
         while ( framesRead != 0 );

         // Close the wavFile
        try
        {
            wavFile.close();
        }
        catch ( Exception e ) {}

        return output;
    }
}
