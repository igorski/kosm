package nl.igorski.lib.framework.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 7/4/12
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 *
 *
 * an INotification Object has a name identifier
 * and an optional Body containing a Value Object
 */
public interface INotification
{

    /**
     * get name of the notification
     * @return {String}
     */
    public String getName();

    /**
     * get optional body of the notification
     * @return {Object}
     */
    public Object getBody();
}
