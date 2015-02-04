package nl.igorski.lib.framework.interfaces;

import android.content.Context;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public interface IProxy
{
    /**
     * a proxy must have a unique identifier name
     * used for registering / retrieving from the core
     * @return {String}
     */
    public String getName();

    /**
     * a proxy must have a link to the Context
     * this allows ICommands to fire native message windows
     * @return
     */
    public Context getContext();
}
