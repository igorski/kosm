package nl.igorski.lib.framework.observer;

import nl.igorski.lib.framework.interfaces.INotification;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 7/4/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Notification implements INotification
{
    private String _name;

    private Object _body;

    public Notification( String aName, Object aBody )
    {
        _name = aName;
        _body = aBody;
    }

    public Notification( String aName )
    {
        _name = aName;
    }

    /* public */

    public String getName()
    {
        return _name;
    }

    public Object getBody()
    {
        return _body;
    }
}
