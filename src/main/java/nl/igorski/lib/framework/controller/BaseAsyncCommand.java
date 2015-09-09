package nl.igorski.lib.framework.controller;

import nl.igorski.lib.framework.interfaces.IAsyncCommand;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.framework.interfaces.IAsyncCommandHandler;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:16
 * To change this template use File | Settings | File Templates.
 */
public class BaseAsyncCommand extends BaseSimpleCommand implements IAsyncCommand
{
    private IAsyncCommandHandler _cancelHandler;

    public BaseAsyncCommand( )
    {
        super();
    }

    /* public */

    public void execute()
    {
        throw new Error( "method 'execute' not overridden in ICommand implementation" );
    }

    public void setCancelHandler( IAsyncCommandHandler cancelHandler )
    {
        _cancelHandler = cancelHandler;
    }

    /* protected */

    protected void commandCancel()
    {
        if ( _cancelHandler != null )
            _cancelHandler.handleCancel( this );

        Core.unregisterCommand(this);
    }
}
