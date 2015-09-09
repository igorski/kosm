package nl.igorski.lib.utils.time;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class DateTool
{
    /**
     * returns the current time as a Unix timestamp
     * @return {int}
     */
    public static int getCurrentTimestamp()
    {
        return ( int ) ( System.currentTimeMillis() / 1000L );
    }
}
