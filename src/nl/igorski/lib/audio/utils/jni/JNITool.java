package nl.igorski.lib.audio.utils.jni;

import nl.igorski.lib.audio.nativeaudio.NativeAudioEngine;
import nl.igorski.lib.audio.nativeaudio.SWIGTYPE_p_int;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 11-06-13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public final class JNITool
{
    public static SWIGTYPE_p_int javaIntArrayToC( int[] aArray )
    {
        final int aArrayLength = aArray.length;
        SWIGTYPE_p_int output = NativeAudioEngine.new_int_array( aArrayLength );

        for ( int i = 0; i < aArrayLength; ++i )
            NativeAudioEngine.int_array_setitem( output, i, aArray[ i ]);

        return output;
    }

    public static int[] cIntArrayToJava( SWIGTYPE_p_int aArray, int aArrayLength )
    {
        final int[] output = new int[ aArrayLength ];

        for ( int i = 0; i < aArrayLength; ++i )
            output[ i ] = NativeAudioEngine.int_array_getitem( aArray, i );

        return output;
    }
}
