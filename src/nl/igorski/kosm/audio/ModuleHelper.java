package nl.igorski.kosm.audio;

import nl.igorski.lib.audio.nativeaudio.Arpeggiator;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 20-03-14
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public final class ModuleHelper
{
    public static int getMinimumArpeggiatorStep()
    {
        return 64; // 64th note
    }

    public static int getMaximumArpeggiatorStep()
    {
        return 2;  // twice each bar
    }

    public static void updateArpeggiatorTempo( Arpeggiator arpeggiator, float oldTempo, float newTempo )
    {
        float stepSize = ( float ) arpeggiator.getStepSize();

        arpeggiator.setStepSize( ( int ) (( stepSize / newTempo ) * oldTempo ));
    }
}
