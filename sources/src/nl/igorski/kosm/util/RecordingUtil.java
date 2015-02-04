package nl.igorski.kosm.util;

import android.text.format.Time;
import nl.igorski.lib.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 27-02-13
 * Time: 23:07
 * To change this template use File | Settings | File Templates.
 */
public final class RecordingUtil
{
    public static String generateFilename()
    {
        Time today = new Time( Time.getCurrentTimezone());
        today.setToNow();

        final String theTime  = today.format( "%k" ) + "h" + today.format( "%M" ) + "m" + today.format( "%S" );

        return "REC" + today.year + "-" + StringUtil.addLeadingZero(( today.month + 1 ), 1 ) + "-" + StringUtil.addLeadingZero( today.monthDay, 1 ) + " " + theTime + ".wav";
    }
}
