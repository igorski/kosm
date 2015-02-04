package nl.igorski.lib.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-09-12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public final class StringUtil
{
    public static String addLeadingZero( String aString, int aAmountOfZeros )
    {
        if ( aString.length() == 1 )
        {
            for ( int i = 0; i < aAmountOfZeros; ++i ) {
                aString = "0" + aString;
            }
        }
        return aString;
    }

    public static String addLeadingZero( int aInt, int aAmountOfZeroes )
    {
        return addLeadingZero( aInt + "", aAmountOfZeroes );
    }
}
