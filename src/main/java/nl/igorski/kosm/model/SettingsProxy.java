package nl.igorski.kosm.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import nl.igorski.kosm.model.vo.VOSetting;
import nl.igorski.lib.framework.interfaces.IProxy;
import nl.igorski.lib.utils.storage.database.interfaces.IEntity;
import nl.igorski.lib.utils.storage.database.DataBase;
import nl.igorski.kosm.definitions.database.Tables;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class SettingsProxy extends DataBase implements IProxy
{
    /* constants */

    public static final String NAME           = "SettingsProxy";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kosm_settings.db";

    public SettingsProxy( Context aContext )
    {
        super( aContext, Tables.TABLE_SETTINGS, DATABASE_NAME, DATABASE_VERSION );
        firstRunCheck();
    }

    /* public methods */

    public String getName()
    {
        return NAME;
    }

    public boolean addOrUpdateSetting( String aKey, String aValue )
    {
        ContentValues values = new ContentValues();

        values.put( "value", aValue );

        final VOSetting setting = getSetting( aKey );
        boolean result          = false;

        try
        {
            // are we adding a new setting?
            if ( setting == null )
            {
                values.put( "key", aKey );
                result = insertRow( values ) > 0;
            }
            else
            {
                // nope... updating
                result = updateRow( setting.id, values );
            }
        }
        catch ( Exception e ) {}    // most likely too fast switching

        return result;
    }

    public VOSetting getSetting( int id )
    {
        Cursor cursor = getRowById( id );

        if ( cursor != null )
            cursor.moveToFirst();
        else
            return null;

        return ( VOSetting ) parseEntity( cursor );
    }

    public VOSetting getSetting( String aKey )
    {
        Cursor cursor = getRowByColumn( "key", aKey );

        if ( cursor != null )
            cursor.moveToFirst();
        else
            return null;

        return ( VOSetting ) parseEntity( cursor );
    }

    public boolean deleteSetting( int id )
    {
        return deleteRow( id );
    }

    public boolean deleteSetting( String aKey )
    {
        return deleteByColumn( "key", aKey );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        super.onCreate( db );

        // open the table if it didn't exist
        final boolean created = createTable( this._table, Tables.getColumnsByTable( this._table ));

        // set the fixtures when the table was newly created
//        if ( created )
//            processFixtures();
    }

    /* protected methods */

    @Override
    protected IEntity parseEntity( Cursor cursor )
    {
        return parseSimpleEntity( cursor );
    }

    /* private methods */

    /**
     * @param cursor {Cursor}
     * @return {VOSetting}
     */

    private VOSetting parseSimpleEntity( Cursor cursor )
    {
        return new VOSetting( Integer.parseInt( cursor.getString( 0 )),
                              cursor.getString( 1 ),
                              cursor.getString( 2 ));
    }

    private void firstRunCheck()
    {
        try
        {
            // first launch fun (shows note about performance)
//            if ( getSetting( SettingsDefinitions.SETTING_FIRST_RUN_WARNING ) == null )
//            {
//                Confirm.singleButtonConfirm( getContext(), R.string.ins_title_performance, R.string.ins_performance );
//                addOrUpdateSetting( SettingsDefinitions.SETTING_FIRST_RUN_WARNING, "true" );
//            }
        }
        catch ( Exception e ) {}
    }
}
