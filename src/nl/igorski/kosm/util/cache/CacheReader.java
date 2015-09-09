package nl.igorski.kosm.util.cache;

import nl.igorski.lib.utils.storage.FileSystem;
import nl.igorski.kosm.config.Config;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-09-12
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public final class CacheReader
{
    /**
     * returns all cached files created during recording
     * the WAVWriter can use this list to construct a single WAVE file
     *
     * @return {String[]}
     */
    public static String[] getCachedRecording()
    {
        return FileSystem.getFiles( Config.CACHE_FOLDER, true );
    }
}
