package nl.igorski.kosm.model.vo;

import nl.igorski.lib.utils.storage.database.interfaces.IEntity;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 08-10-12
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public final class VOSetting implements IEntity
{
    public int id;

    public String key;

    public String value;

    public VOSetting( int aId, String aKey, String aValue )
    {
        id    = aId;
        key   = aKey;
        value = aValue;
    }

    /* public */

    public String getType()
    {
        return "VOSetting";
    }
}
