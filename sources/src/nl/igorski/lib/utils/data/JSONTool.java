package nl.igorski.lib.utils.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 *
 * seeing how the org.json.JSONObject requires
 * the catching of possible exceptions, we wrap the
 * most common JSON operations in this static class
 */
public final class JSONTool
{
    /**
     * creates a new JSONObject from a JSON String
     * @param aJSON {String} JSON data
     * @return {JSONObject}
     */
    public static JSONObject createFromString( String aJSON )
    {
        JSONObject json = new JSONObject();

        try
        {
            json = new JSONObject( aJSON );
        }
        catch ( JSONException e ) {}

        return json;
    }

    public static String toString( JSONObject aJSONObject )
    {
        return aJSONObject.toString();
    }

    /* retrieve values stored in the JSON Object, cast to their expected types */

    public static JSONArray getArray( JSONObject aJSONObject, String aKey )
    {
        JSONArray out = null;

        try
        {
            out = aJSONObject.getJSONArray( aKey );
        }
        catch( JSONException e ) {};

        return out;
    }

    public static boolean getBoolean( JSONObject aJSONObject, String aKey )
    {
        boolean out = false;

        try
        {
            out = aJSONObject.getBoolean( aKey );
        }
        catch ( JSONException e ) {}

        return out;
    }

    public static double getDouble( JSONObject aJSONObject, String aKey )
    {
        double out = 0;

        try
        {
            out = aJSONObject.getDouble( aKey );
        }
        catch ( JSONException e ) {}

        return out;
    }

    public static int getInt( JSONObject aJSONObject, String aKey )
    {
        int out = 0;

        try
        {
            out = aJSONObject.getInt( aKey );
        }
        catch ( JSONException e ) {}

        return out;
    }

    public static Object getObject( JSONObject aJSONObject, String aKey )
    {
        return getValue( aJSONObject, aKey );
    }

    public static JSONObject getObjectFromArray( JSONArray aArray, int aIndex )
    {
        JSONObject out = null;

        try
        {
            out = aArray.getJSONObject( aIndex );
        }
        catch( JSONException e ) {}

        return out;
    }

    public static String getString( JSONObject aJSONObject, String aKey )
    {
        String out = "";

        try
        {
            out = aJSONObject.getString( aKey );
        }
        catch ( JSONException e ) {}

        return out;
    }

    /* Array methods */

    public static int getIntFromArray( JSONArray aArray, int aIndex )
    {
        int out = 0;

        try
        {
            out = aArray.getInt( aIndex );
        }
        catch( JSONException e ) {}

        return out;
    }

    /* set a value in the JSON Object */

    public static void setValue( JSONObject aJSONObject, String aKey, Object aValue )
    {
        try
        {
            aJSONObject.put( aKey, aValue );
        }
        catch ( JSONException e ) {}
    }

    /* private */

    private static Object getValue( JSONObject aJSONObject, String aKey )
    {
        Object value = "";

        try
        {
            value = aJSONObject.get( aKey );
        }
        catch ( JSONException e ) {}

        return value;
    }
}
