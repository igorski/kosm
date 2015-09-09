package nl.igorski.kosm.definitions;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import nl.igorski.lib.listeners.ICompleteListener;
import nl.igorski.kosm.view.physics.components.AudioParticle;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.utils.debugging.DebugTool;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 15-04-12
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */
public final class Assets
{
    // general sequencer appearance

    public static boolean IS_EXTRA_LARGE    = false; // whether the device has a very high resolution
    public static boolean IS_SMALL          = false; // whether the device has a very low resolution (Samsung Gio/Pocket)
    public static boolean IS_TABLET         = false; // whether the device's physical size is in tablet range
    public static boolean IS_LARGE_TABLET   = false; // whether the device screen is 10" or larger
    public static boolean USE_LARGE_MARGINS = false;

    /* fonts and text sizes (calculated on init) */

    public static Typeface DEFAULT_FONT;
    public static int TEXT_SIZE_HEADER = 0;
    public static int TEXT_SIZE_NORMAL = 0;

    /* colors */

    public final static int COLOR_KOSM_BLUE = Color.parseColor( "#FF3AB3B5" );

    /* cached assets */

    /**
     * prepares the application to work on multiple
     * resolutions by querying the available size
     * and calculating all "constants"
     * @param aContext {Context}
     */
    public static void init( final Context aContext, final ICompleteListener listener )
    {
        final Resources resources = aContext.getResources();

        WindowManager wm     = ( WindowManager ) aContext.getSystemService( Context.WINDOW_SERVICE );
        Display display      = wm.getDefaultDisplay();

        // calculate the screens metrics
        final DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( metrics );

        final int SCREEN_WIDTH  = metrics.widthPixels;
        final int SCREEN_HEIGHT = metrics.heightPixels;

        // Samsung Galaxy S Plus:   DisplayMetrics{density=1.5, width=800, height=480,  scaledDensity=1.5, xdpi=160.42105, ydpi=160.0} @ DPI: 240
        // Sony Experia:            DisplayMetrics{density=2.0, width=1280, height=720, scaledDensity=2.0, xdpi=345.0566,  ydpi=342.23157 DPI: 320}
        // Google Nexus 4:          DisplayMetrics{density=2.0, width=1196, height=768, scaledDensity=2.0, xdpi=320.0, ydpi=320.0} @ DPI: 320 !! EMULATED !!
        // Google Nexus 5:          DisplayMetrics{density=3.0, width=1794, height=1080, scaledDensity=3.0, xdpi=442.451, ydpi=443.345} @ DPI: 480
        // Google Nexus 7:          DisplayMetrics{density=1.33125, width=1280, height=736, scaledDensity=1.33125, xdpi=213.0, ydpi=213.0} @ DPI: 213 !! EMULATED !!
        // Huawei Ascend Y300:      DisplayMetrics{density=1.5, width=800, height=480, scaledDensity=1.5, xdpi=240.0, ydpi=240.0} @ DPI: 240
        // Samsung Galaxy Tab 7.1 : DisplayMetrics{density=1.5, width=1024, height=600, scaledDensity=1.5, xdpi=168.89351, ydpi=169.33333} @ DPI: 240
        // Samsung Galaxy Tab 10.1: DisplayMetrics{density=1.0, width=1280, height=800, scaledDensity=1.0, xdpi=160.15764, ydpi=160.0} @ DPI: 160
        // Samsung Gio            : DisplayMetrics{density=1.0, width=480, height=320, scaledDensity=1.0, xdpi=160.0, ydpi=160.0} @ DPI: 160 !! EMULATED !!
        // Samsung Galaxy Pocket  : DisplayMetrics{density=0.75, width=320, height=240, scaledDensity=0.75, xdpi=120.0, ydpi=120.0} @ DPI: 120

        //Determine screen size
        if (( resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK ) == Configuration.SCREENLAYOUT_SIZE_LARGE )
            DebugTool.log( "screen layout size > large" );

        else if (( resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK ) == Configuration.SCREENLAYOUT_SIZE_NORMAL )
            DebugTool.log( "screen layout size > normal" );

        else if (( resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK ) == Configuration.SCREENLAYOUT_SIZE_SMALL )
            DebugTool.log( "screen layout size > small" );

        else
            DebugTool.log( "screen layout size > neither large, normal or small" );

        DebugTool.log( metrics.toString() + " @ DPI: " + metrics.densityDpi );

        if ( metrics.densityDpi > 240 && metrics.heightPixels > 600 ) // Sony Experia, Nexus 4...
            IS_EXTRA_LARGE = true;

        else if ( metrics.density <= 1.0 && metrics.widthPixels < 500 ) // Samsung Gio / Galaxy Pocket
            IS_SMALL = true;

        IS_TABLET         = checkIfIsTabletDevice( aContext, metrics );
        USE_LARGE_MARGINS = IS_TABLET;  // TODO : likely to occur on other resolutions ?

        DebugTool.log( "Kosm running on tablet device:" + IS_TABLET + ", on >= 10 inch screen: " + IS_LARGE_TABLET + " or on small screen:" + IS_SMALL );

        // we start a new local thread to crunch the decoding of assets
        // the callback handler is invoked upon completion

        new Thread(
        new Runnable()
        {
            public void run()
            {
                // pass this to the "decodeResource"-methods to prevent the BitmapFactory from upscaling images
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                final DisplayMetrics theMetrics = resources.getDisplayMetrics();

                // the orb sizes
                AudioParticle.MIN_RADIUS  = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 5,  theMetrics );
                AudioParticle.MAX_RADIUS  = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 25, theMetrics );
                AudioParticle.STROKE_SIZE = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 2,  theMetrics );

                prepareFonts( aContext, theMetrics );

                if ( IS_SMALL )
                    optimizeForSmallScreens();

                if ( IS_LARGE_TABLET )
                    optimizeForLargeScreens();

                Core.getActivity().runOnUiThread( new Runnable()
                {
                    public void run()
                    {
                        listener.handleComplete( true );
                    }
                });

            }
        }).start();
    }

    /* private methods */

    private static void prepareFonts( Context aContext, DisplayMetrics aMetrics )
    {
        DEFAULT_FONT = Typeface.createFromAsset( aContext.getAssets(), "fonts/Comfortaa-Bold.ttf" );

        TEXT_SIZE_HEADER = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 19, aMetrics );
        TEXT_SIZE_NORMAL = ( int ) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 14, aMetrics );
    }

    private static void optimizeForSmallScreens()
    {
        // nowt...
    }

    private static void optimizeForLargeScreens()
    {
        // nowt...
    }

    private static boolean checkIfIsTabletDevice( Context activityContext, DisplayMetrics metrics )
    {
        final int widthPixels  = metrics.widthPixels;
        final int heightPixels = metrics.heightPixels;

        // as obscure tablets misrepresent their density as hdpi
        // or are 800 x 480 on a 7: screen, let's calculate the dimensions in inches

        final float widthDpi     = metrics.xdpi;
        final float heightDpi    = metrics.ydpi;
        final float widthInches  = widthPixels / widthDpi;
        final float heightInches = heightPixels / heightDpi;

        final double diagonalInches = Math.sqrt(( widthInches * widthInches ) + ( heightInches * heightInches ));

        // we round the diagonal off as Nexus 7" reports 6.931994478851955
        // 10" tablet reports 9.278469504066424 for its diagonal funnily enough...
        //DebugTool.log( "device diagonal: " + diagonalInches + " @ round : " + Math.round( diagonalInches ));

        if ( Math.round( diagonalInches ) >= 9 /*10*/ )
            IS_LARGE_TABLET = true;

        return ( Math.round( diagonalInches ) >= 7 );
    }
}
