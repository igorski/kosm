package nl.igorski.kosm.controller.menu;

import nl.igorski.lib.framework.controller.BaseAsyncCommand;

/**
 * Created by igorzinken on 09-04-17.
 */
public class AnimatedMenuCommand extends BaseAsyncCommand {
    public static boolean wfAnimationLocked = false; // only one animation instance per menu at a time!
    public static boolean fxAnimationLocked = false; // only one animation instance per menu at a time!

    public AnimatedMenuCommand() {
        super();
    }
}
