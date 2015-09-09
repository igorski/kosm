package nl.igorski.kosm.controller.render;

import nl.igorski.kosm.util.RecordingUtil;
import nl.igorski.kosm.util.cache.CacheReader;
import nl.igorski.kosm.util.cache.CacheWriter;
import nl.igorski.lib.audio.utils.WAVWriter;
import nl.igorski.lib.framework.controller.BaseAsyncCommand;
import nl.igorski.lib.utils.notifications.Progress;
import nl.igorski.lib.utils.time.Timeout;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.notifications.Alert;
import nl.igorski.lib.utils.storage.FileSystem;

import nl.igorski.kosm.R;
import nl.igorski.kosm.config.Config;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 05-07-12
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
public class WriteRecordingCommand extends BaseAsyncCommand
{
    private boolean _recordingCompleted = false;

    /* public methods */

    public void execute( INotification aNotification )
    {
        DebugTool.log( "EXECUTING WRITE RECORDING COMMAND" );

        Progress.initDialog(Core.getContext(), R.string.title_render, R.string.msg_render);

        write();
    }

    protected void write()
    {
        DebugTool.log( "WRITING CACHED OUTPUT TO WAVE FILE" );

        final String directory = Config.OUTPUT_DIRECTORY;
        final String fileName  = RecordingUtil.generateFilename();

        WAVWriter.mergeWAVFiles(CacheReader.getCachedRecording(), directory + File.separator + FileSystem.sanitizeFileName(fileName));
        CacheWriter.flushCache();

        DebugTool.log( "CACHED OUTPUT WRITTEN TO WAVE FILE" );
        _recordingCompleted = true;

        // w/ short recordings the writing has completed before
        // the Progress is initialized, rendering dismissal useless
        // and leaving us a with a non-removable Progress window!
        Timeout.setTimeout(2500, new Runnable() {
            public void run() {
                if (_recordingCompleted) {
                    Progress.dismiss();
                }
            }
        }, true);

        Progress.dismiss();
        Alert.show( Core.getContext(), R.string.rec_saved );

        commandComplete();
    }
}
