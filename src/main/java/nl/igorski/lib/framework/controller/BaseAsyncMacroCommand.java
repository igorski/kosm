package nl.igorski.lib.framework.controller;

import android.util.Log;
import nl.igorski.lib.framework.interfaces.*;
import nl.igorski.lib.framework.Core;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class BaseAsyncMacroCommand implements IMacroCommand, IAsyncCommandHandler
{
    private Vector<ICommand> _subCommands;

    private INotification _note;

    public BaseAsyncMacroCommand()
    {
        initializeMacroCommand();
    }

    /* public */

    public void initializeMacroCommand()
    {
        // add subcommands here in your subclass
        //addSubCommand( ... );
    }

     // do not override
    public void execute( INotification aNotification )
    {
        _note = aNotification; // hold reference to the Notification

        nextCommand();         // process the command queue
    }

    public void setCompleteHandler( ICommandHandler completeHandler )
    {
        // unused in a macro chain... as "chainComplete" is invoked
    }

    /* event handlers */

    /* listen for individual sub command completes and cancels */

    public void handleComplete( ICommand commandRef )
    {
        nextCommand();
    }

    // you may override the cancel handler for specific uses, don't forget to invoke super!
    // TODO: do we want to map a cancel command to a cancelled subcommand ?
    public void handleCancel( IAsyncCommand commandRef )
    {
        // cancel the remaining chain
        chainCancelled();
    }

    /* protected */

    protected void addSubCommand( ICommand commandRef )
    {
        if ( _subCommands == null )
            _subCommands = new Vector<ICommand>();

        _subCommands.add( commandRef );

        if ( commandRef instanceof IAsyncCommand)
        {
            ((IAsyncCommand) commandRef ).setCancelHandler( this );
        }
        commandRef.setCompleteHandler( this );
    }

    /* private */

    private void nextCommand()
    {
        // no (more) subcommands in the queue ? we're finished
        if ( _subCommands == null || _subCommands.size() == 0 )
        {
            chainComplete();
        }
        else
        {
            // get the next command in line
            ICommand commandClass = _subCommands.get( 0 );
            _subCommands.remove( commandClass );

            commandClass.execute( _note );
        }
    }

    private void chainComplete()
    {
        Log.d( "SYNTH", "CHAIN COMPLETED");
        Core.unregisterCommand(this);
    }

    /**
     * as a MacroCommand can have multiple sub commands
     * it is better to specify cancel handlers in each
     * subcommand for more precise error reporting, as
     * such a cancel of the chain occurs when commandCancel
     * has been invoked on a subcommand, it merely unregisters
     * this macro command from the Core
     */
    private void chainCancelled()
    {
        Log.d("SYNTH", "CHAIN CANCELLED");
        Core.unregisterCommand(this);
    }
}
