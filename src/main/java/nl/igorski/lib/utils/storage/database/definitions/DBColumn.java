package nl.igorski.lib.utils.storage.database.definitions;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/18/12
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public final class DBColumn
{
    /**
     * DBColumn describes the properties of a single
     * table column, used as part of a DBColumn[] Array
     * this can be used by the DataBase class to map
     * the contents of a table
     */
    public String COLUMN_NAME;

    public String TYPE;

    public String PROPERTY;

    /**
     * @param aColumnName  {String} name of the column
     * @param aColumnType  {String} required, the column type ( "INTEGER", "TEXT", etc. )
     * @param aProperty    {String} optional property such as "PRIMARY KEY" or "INDEX"
     *                     can also be used to chain properties and restrictions such
     *                     as : "INDEX NOT NULL" as long as it is separated by a space
     *                     see SQL definition
     */
    public DBColumn( String aColumnName, String aColumnType, String aProperty )
    {
        COLUMN_NAME = aColumnName;
        TYPE        = aColumnType;
        PROPERTY    = aProperty;
    }

    public DBColumn( String aColumnName, String aColumnType )
    {
        COLUMN_NAME = aColumnName;
        TYPE        = aColumnType;
    }
}
