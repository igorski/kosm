package nl.igorski.kosm.util.cache;

import nl.igorski.lib.utils.StringUtil;
import nl.igorski.lib.utils.storage.FileSystem;
import nl.igorski.kosm.config.Config;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-09-12
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public final class CacheWriter
{
    /**
     * rename the last recorded fragment so it is correctly sorted using
     * alphanumerical directory sorting (for use w/ merging when recording completes)
     *
     * @param aLastRecordedBufferIndex {int} the index of the last buffer fully
     *                                 recorded by the native aduio engine
     */
    public static void renameLastFragment( int aLastRecordedBufferIndex )
    {
        final String cacheFolder = Config.CACHE_FOLDER + File.separator;

        FileSystem.rename( cacheFolder + aLastRecordedBufferIndex,
                           cacheFolder + Config.RECORDING_CACHE_PREFIX + StringUtil.addLeadingZero( aLastRecordedBufferIndex + "", 5 ));
    }

    public static void createOutputFolders()
    {
        final String cacheDirectory = Config.CACHE_FOLDER;
        FileSystem.makeDir( cacheDirectory );
    }

    public static boolean flushCache()
    {
        return FileSystem.cleanDir( Config.CACHE_FOLDER );
    }

    /* private methods */

    private static String createFileName()
    {
        // prepare output folders...
        createOutputFolders();

        // create a new file name (for alphanumerical sorting)
        // by taking the amount of existing files into account
        return Config.CACHE_FOLDER + File.separator + Config.RECORDING_CACHE_PREFIX + StringUtil.addLeadingZero( FileSystem.countFiles( Config.CACHE_FOLDER ) + "", 5 );
    }
}
