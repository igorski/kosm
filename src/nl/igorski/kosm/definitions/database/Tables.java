package nl.igorski.kosm.definitions.database;

import nl.igorski.lib.utils.storage.database.definitions.DBColumn;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
public final class Tables
{
    public static String TABLE_SETTINGS     = "settings";

    private static DBColumn[] COLUMNS_SETTINGS = {
                                                     new DBColumn( "id",    "INTEGER", "PRIMARY KEY" ),
                                                     new DBColumn( "key",   "TEXT" ),
                                                     new DBColumn( "value", "TEXT" ),
                                                 };

    public static DBColumn[] getColumnsByTable( String aTable )
    {
        if ( aTable.equals( TABLE_SETTINGS ))
            return COLUMNS_SETTINGS;

        return null;
    }
}
