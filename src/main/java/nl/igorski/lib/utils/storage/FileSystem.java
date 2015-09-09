/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 Igor Zinken - http://www.igorski.nl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package nl.igorski.lib.utils.storage;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 27-06-12
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 *
 * FileSystem provides convenience methods for performing
 * CRUD operations on folders and files, automatically
 * resolving the writeable root for the application
 */
public final class FileSystem
{
    /**
     * opens / creates (if didn't exist) a file
     * on the devices filesystem
     *
     * @param aFilename {String}
     * @return {File}
     */
    public static File open( String aFilename )
    {
        return new File( getWritableRoot() + File.separator + aFilename );
    }

    public static boolean exists( String aFilename )
    {
        return open( aFilename ).exists();
    }

    public static boolean rename( String aSourceFile, String aTargetFile )
    {
        if ( !exists( aSourceFile ))
            return false;

        File from = open( aSourceFile );
        File to   = open( aTargetFile );

        return from.renameTo( to );
    }

    public static boolean delete( String aFilename )
    {
        return ( exists( aFilename ) && open( aFilename ).delete());
    }

    /* directory manipulation */

    public static boolean makeDir( String aDirectoryName )
    {
        File directory = new File( getWritableRoot() + File.separator + aDirectoryName );

        // build directory structure if it didn't exist
        return directory.mkdirs();
    }

    public static int countFiles( String aDirectoryName )
    {
        File directory = new File( getWritableRoot() + File.separator + aDirectoryName );

        if ( directory.isDirectory())
        {
            return directory.list().length;
        }
        return 0;
    }

    public static String[] getFiles( String aDirectoryName, boolean returnPathInFileList )
    {
        File directory = new File( getWritableRoot() + File.separator + aDirectoryName );

        // sort files alphabetically
        Comparator<? super String> orderAlphabetically = new Comparator<String>()
        {
            public int compare( String file1, String file2 )
            {
                return String.valueOf( file1.toLowerCase()).compareTo( file2.toLowerCase());
            }
        };

        if ( directory.isDirectory())
        {
            String[] files = directory.list();

            Arrays.sort( files, orderAlphabetically );

            if ( returnPathInFileList )
            {
                for ( int i = 0, l = files.length; i < l; ++i )
                    files[ i ] = aDirectoryName + File.separator + files[ i ];
            }
            return files;
        }
        return new String[ 0 ];
    }

    public static boolean cleanDir( String aDirectoryName )
    {
        File directory = new File( getWritableRoot() + File.separator + aDirectoryName );

        if ( directory.isDirectory())
        {
            String[] children = directory.list();

            for (int i = 0; i < children.length; i++ )
            {
                new File( directory, children[ i ]).delete();
            }
            return true;
        }
        return false;
    }

    /* file system available space */

    public static long getFreeSpaceInBytes()
    {
        StatFs stat = new StatFs( getWritableRoot().getPath());

        long blockSize;
        long availableBlocks;

        // API level 18 available ?
        if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 )
        {
            blockSize       = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }
        else {
            blockSize       = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }

    public static double getFreeSpaceInMegaBytes()
    {
        // one binary megabyte equals 1,048,576 bytes
        return ( double )( getFreeSpaceInBytes()) / 1048576.0;
    }

    public static double getFreeSpaceInGigaBytes()
    {
        // one binary gigabyte equals 1,073,741,824 bytes
        return ( double ) ( getFreeSpaceInBytes()) / 1073741824.0;
    }

    /**
     * query the file system if it has space for a requested file size
     *
     * @param aFileSize {int} file size in bytes
     * @return {boolean}
     */
    public static boolean hasSpaceFor( int aFileSize )
    {
        return getFreeSpaceInBytes() >= aFileSize;
    }

    /* helper methods for creating files / directories */

    /**
     * makes sure a file has no illegal characters
     * (or empty spaces though these aren't problematic)
     *
     * @param aFileName {String} requested filename
     * @return {String} sanitized filename
     */
    public static String sanitizeFileName( String aFileName )
    {
        aFileName = aFileName.replace( " ", "_" );

        final String[] illegalChars = { "\\", "/", "'", "\"", ":", "&", "!", "?", "@", "#", "$", "%", "^", "*", "[", "]","{", "}", "," };

        for ( final String ch : illegalChars )
            aFileName = aFileName.replace( ch, "" );

        return aFileName;
    }

    public static boolean canWrite()
    {
        File f = getWritableRoot();
        return f != null && f.canWrite();
    }

    public static boolean canRead()
    {
        File f = getWritableRoot();
        return f != null && f.canRead();
    }

    /**
     * retrieve the path to the writable storage on the current device
     * if a SD card is mounted / external storage is emulated, this is returned
     * otherwise root directory is returned.
     *
     * you can query the "canWrite" and "canRead" methods in this class to
     * find out whether this is of any use on the current device within the current application!
     *
     * @return {File}
     */
    public static File getWritableRoot()
    {
        String extState = Environment.getExternalStorageState();

        // can we read and write the external media ?
        if ( Environment.MEDIA_MOUNTED.equals( extState ))
            return Environment.getExternalStorageDirectory();

        return Environment.getRootDirectory();
    }
}
