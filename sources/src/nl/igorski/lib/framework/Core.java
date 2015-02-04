package nl.igorski.lib.framework;

import android.app.Activity;
import android.content.Context;
import nl.igorski.lib.framework.interfaces.ICommand;
import nl.igorski.lib.framework.interfaces.IProxy;
import nl.igorski.lib.framework.observer.Notification;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:25
 * To change this template use File | Settings | File Templates.
 */
public class Core
{
    private static Vector<IProxy> proxies;

    private static Vector<ICommand> commands;

    private static Context _context;

    /* public */

    /**
     * initialize a core when starting a (new) Activity
     * pass the Activity along as the argument so we
     * can always get a reference to the current Context
     *
     * @param aContext {Context}
     */
    public static void init( Context aContext )
    {
        _context = aContext;
    }

    public static Context getContext()
    {
        return _context;
    }

    public static Activity getActivity()
    {
        return ( Activity ) _context;
    }

    /**
     * fire a command, this registers the command in the Core
     * to prevent it from being garbage collected until it is finished
     *
     * usage: Core.notify( new ICommandClass());
     *
     * TODO: create a more elegant solution with String notifications
     * mapping commands to String constants
     *
     * @param commandRef {ICommand}
     */
    public static void notify( ICommand commandRef )
    {
        registerCommand( commandRef );
        commandRef.execute( new Notification( "TODO:StringNameBASED" ));
    }

    public static void notify( ICommand commandRef, Object notificationBody )
    {
        registerCommand( commandRef );
        commandRef.execute( new Notification( "TODO:StringNameBASED", notificationBody ));
    }

    /**
     * called by an ICommand on completion so
     * its reference can be cleared, note that subcommands
     * of a MacroCommand chain are stored in the MacroCommand
     * itself (the MacroCommand does have a reference in the Core)
     *
     * @param commandRef {ICommand}
     */
    public static void unregisterCommand( ICommand commandRef )
    {
        if ( commands != null )
        {
            // note we check of the commandRef exists in the Vector
            // as it's possible we're currently completing a
            // command that is part of a Macro chain (and thus
            // its reference is held there)

            if ( commands.contains( commandRef ))
                commands.remove( commandRef );
        }
    }

    /**
     * registers a new Proxy in the core
     *
     * @param aProxy {IProxy}
     * @return {IProxy} the registered proxy
     */
    public static IProxy registerProxy( IProxy aProxy )
    {
        if ( proxies == null )
        {
            proxies = new Vector<IProxy>();
        }
        else
        {
            final IProxy existed = retrieveProxy( aProxy.getName());

            if ( existed != null )
                return existed;
        }
        proxies.add( aProxy );

        return aProxy;
    }

    /**
     * unregisters an obsolete proxy
     *
     * @param aProxy {IProxy}
     * @return {boolean}
     */
    public static boolean unregisterProxy( IProxy aProxy )
    {
        if ( proxies != null )
        {
            final IProxy instance = retrieveProxy( aProxy.getName());

            if ( instance != null )
            {
                proxies.remove( instance );
                return true;
            }
        }
        return false;
    }

    /**
     * retrieve a register proxy from the framework Core
     * @param aProxyName {String} the proxy to register
     * @return {IProxy}
     */
    public static IProxy retrieveProxy( String aProxyName )
    {
        if ( proxies != null )
        {
            for ( final IProxy p : proxies )
            {
                if ( p.getName().equals( aProxyName ))
                    return p;
            }
        }
        return null;
    }

    /* private */

    private static void registerCommand( ICommand commandRef )
    {
        if ( commands == null )
            commands = new Vector<ICommand>();

        commands.add( commandRef );
    }
}
