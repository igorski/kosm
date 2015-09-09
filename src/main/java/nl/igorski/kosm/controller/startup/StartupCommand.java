package nl.igorski.kosm.controller.startup;

import nl.igorski.kosm.config.Config;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.storage.FileSystem;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 24-01-15
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public final class StartupCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log("STARTUP COMMAND");

        // create output folders
        FileSystem.makeDir( Config.OUTPUT_DIRECTORY );

        super.execute( aNote ); // completes command
    }
}
