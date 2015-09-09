package nl.igorski.kosm.config;

import nl.igorski.lib.audio.nativeaudio.BufferUtility;
import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.utils.storage.FileSystem;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 23-09-12
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public final class Config
{
    public final static String OUTPUT_DIRECTORY  = "Kosm";
    public final static String CACHE_FOLDER      = OUTPUT_DIRECTORY + File.separator + "cache";

    public final static String RECORDING_CACHE_PREFIX = "cache_";
    public final static int RECORDING_FRAGMENT_SIZE   = 30000; // 30 seconds in ms

    /**
     * quick query to determine whether the device has space for recording
     * a single fragment of audio at the size defined above
     *
     * @return {boolean}
     */
    public static boolean canRecord()
    {
        // times two as the actual wave files are roughly 2.5 mb for a mono file (of 1.3 mb buffer size)
        final int requiredSize = BufferUtility.millisecondsToBuffer( RECORDING_FRAGMENT_SIZE, MWEngine.SAMPLE_RATE ) * 2;

        // we append 2048 bytes as the written file is (measured @ 12 bytes) larger than the above calculation
        return FileSystem.hasSpaceFor(( requiredSize * MWEngine.BYTES_PER_SAMPLE ) + 2048 );
    }
}
