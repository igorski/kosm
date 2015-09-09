package nl.igorski.lib.utils;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/12/12
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeConverter
{
    /**
     * safely convert a long to integer
     * @param l {long}
     * @return {int}
     */
    public static int longToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException
                ( l + " cannot be cast to int without changing its value." );
        }
        return ( int ) l;
    }
}
