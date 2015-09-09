package nl.igorski.lib.utils.storage.database.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 21-04-12
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
public interface IEntity
{
    /**
     * IEntity is the interface to be implemented by
     * all Entity Objects used by your models
     *
     * the getType method should return a String
     * identifying the Object / Entity type
     *
     * @return {String}
     */
    public String getType();
}
