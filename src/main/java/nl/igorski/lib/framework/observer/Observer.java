package nl.igorski.lib.framework.observer;

import nl.igorski.lib.framework.interfaces.IProxy;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 22:33
 * To change this template use File | Settings | File Templates.
 */
public class Observer
{
    public IProxy getProxy( String aProxyName )
    {
        return Core.retrieveProxy(aProxyName);
    }

    // TODO : implement

    public String[] listNotificationInterest()
    {
        return new String[]{};
    }

    public void handleNotification()
    {

    }
}
