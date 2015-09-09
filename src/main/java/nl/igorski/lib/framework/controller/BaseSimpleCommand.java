package nl.igorski.lib.framework.controller;

import nl.igorski.lib.framework.interfaces.ICommand;
import nl.igorski.lib.framework.interfaces.IProxy;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.framework.interfaces.ICommandHandler;
import nl.igorski.lib.framework.interfaces.INotification;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:16
 * To change this template use File | Settings | File Templates.
 */
public class BaseSimpleCommand implements ICommand
{
    private ICommandHandler _completeHandler;

    public BaseSimpleCommand()
    {

    }

    /* public methods */

    public void execute( INotification aNotification )
    {
        /**
         * add command logic here, don't forget to
         * invoke super() for automatic calling of the complete handler
         */
        commandComplete();
    }

    public void setCompleteHandler( ICommandHandler completeHandler )
    {
        _completeHandler = completeHandler;
    }

    /* protected */

    protected void commandComplete()
    {
        if ( _completeHandler != null )
            _completeHandler.handleComplete( this );

        Core.unregisterCommand( this );
    }

    /**
     * quick getter for retrieving a proxy registered
     * in the framework Core
     *
     * @param aProxyName {String}
     * @return {IProxy}
     */
    protected IProxy getProxy( String aProxyName )
    {
        return Core.retrieveProxy( aProxyName );
    }
}
