package nl.igorski.kosm.definitions;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 16-11-12
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
public final class SequencerModes
{
    public static final int MODE_DEFAULT    = 0;
    public static final int MODE_HOLD       = 1;
    public static final int MODE_360        = 2;
    public static final int MODE_EMITTER    = 3;
    public static final int MODE_RESETTER   = 4;

    public static final int[] MODES         = new int[]{ MODE_DEFAULT, MODE_HOLD, MODE_EMITTER, MODE_360, MODE_RESETTER };
}
