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
package nl.igorski.lib.utils.debugging;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import nl.igorski.lib.utils.storage.FileSystem;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 *
 * A convenience wrapper for logging application states onto
 * the Android debugger, which can be viewed using adb logcat
 */
public final class DebugTool
{
    public static String LOG_TAG = "IGORSKI";

    /**
     * override the default LOG_TAG and use it throughout the
     * application for easy identification in the logcat
     *
     * @param {String} aTag desired tag to use
     */
    public static void registerLogTag( String aTag )
    {
        LOG_TAG = aTag;
    }

    /**
     * dumps the current application heap to a (.HPROF) file in
     * the external storage directory of the device, this file
     * can then be analyzed using MAT or jhat after converting
     * the dump from Dalvik into J2SE HPROF format by running:
     * hprof-conv dump.hprof converted-dump.hprof
     *
     * @param aFileName {String} name of the output file (is stored in the external directory's root)
     * @return {boolean}
     */
    public static boolean dumpHeapToFile( String aFileName )
    {
        try
        {
            android.os.Debug.dumpHprofData( FileSystem.getWritableRoot() + File.separator + aFileName );
        }
        catch ( IOException e ) {
            return false;
        }
        return true;
    }

    /**
     * convenience method for dumping the contents of a MotionEvent
     * directly to the Android Logcat
     * @param aEvent {MotionEvent}
     * @param aTag   {String} used for filtering by the Logcat
     */
    public static void dumpEvent( MotionEvent aEvent, String aTag )
    {
        String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
        "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
        StringBuilder sb = new StringBuilder();

        int action     = aEvent.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append( "event ACTION_" ).append(names[actionCode]);

        if ( actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP )
        {
            sb.append( "(pid " ).append( action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT );
            sb.append( ")" );
        }
        sb.append("[" );
        for ( int i = 0; i < aEvent.getPointerCount(); i++ )
        {
            sb.append( "#" ).append( i );
            sb.append( "(pid " ).append( aEvent.getPointerId( i ));
            sb.append( ")=" ).append( ( int ) aEvent.getX( i ));
            sb.append( "," ).append(  ( int ) aEvent.getY( i ));

            if ( i + 1 < aEvent.getPointerCount())
                sb.append( ";" );
        }
        sb.append( "]" );
        Log.d( aTag, sb.toString());
    }

    /**
     * split a very large string into smaller chunks which
     * are in turn sent to the logcat individually
     *
     * @param aString {String} the String to dump
     */
    public static void dumpLargeString( String aString )
    {
        final int limit = 3000;

        if ( aString.length() > limit )
        {
            log( aString.substring( 0, limit ));
            dumpLargeString( aString.substring( limit ));
        }
        else
        {
            log( aString );
        }
    }

    public static void dumpArray( int[] aArray )
    {
        String out = "";

        for ( int i = 0, l = aArray.length; i < l; ++i )
        {
            out += aArray[ i ];

            if ( i < l - 1 )
                out += ", ";
        }
        Log.d(LOG_TAG, "INT ARRAY > " + out);
    }

    /**
     * draws a red circle indicating where a touch event
     * has been detected
     *
     * @param canvas {Canvas} to draw onto
     * @param x {int} x position of the touch
     * @param y {int} y position of the touch
     */
    public static void drawTouchPoint( Canvas canvas, int x, int y )
    {
        Paint paint = new Paint();
        paint.setColor( Color.RED );

        int circleRadius = 15;

        canvas.drawCircle( x - ( circleRadius / 2 ), y - ( circleRadius / 2 ), circleRadius, paint );
    }

    /**
     * draws a red semi-transparent square to indicate
     * the touchable hit area of a sprite (represented by Rect)
     *
     * @param canvas {Canvas} to draw onto
     * @param rect {Rect} bounding box for a sprite
     */
    public static void drawTouchableArea( Canvas canvas, Rect rect )
    {
        Paint paint = new Paint();
        paint.setColor( Color.RED );
        paint.setAlpha( 50 );

        canvas.drawRect( rect, paint );
    }

    /**
     * draws a blue semi-transparent square to indicate
     * the touchable hit area of a sprite (represented by Rect)
     *
     * @param canvas {Canvas} to draw onto
     * @param rect {RectF} bounding box for a sprite
     */
    public static void drawTouchableArea( Canvas canvas, RectF rect )
    {
        Paint paint = new Paint();
        paint.setColor( Color.BLUE );
        paint.setAlpha( 75 );

        canvas.drawRect( rect, paint );
    }

    /**
     * log a message to the logcat
     * @param aMessage {String}
     */
    public static void log( String aMessage )
    {
        log( aMessage, LOG_TAG);
    }

    /**
     * log a message to the logcat
     * @param aMessage {String} message to log
     * @param aIdOverride {String} optional log_id override
     */
    public static void log( String aMessage, String aIdOverride )
    {
        Log.d( aIdOverride, aMessage );
    }

    /**
     * logs the stack trace of a caught Exception
     * to the logcat
     *
     * @param e {Exception}
     */
    public static void logException( Exception e )
    {
        Log.e( LOG_TAG, e.toString(), e );
    }

    /**
     * get the execution stack trace (up until this method)
     * for debugging purposes
     */
    public static void stackTrace()
    {
        String stack = "";

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for ( int i = 3, l = stackTrace.length; i < l; ++i )
            stack += stackTrace[ i ].toString() + "\n";

        log( stack );
    }
}
