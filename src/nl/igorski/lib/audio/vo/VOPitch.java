package nl.igorski.lib.audio.vo;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/12/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public final class VOPitch
{
    public String note;
    public int octave;
    public double cents;

    public VOPitch( String aNote, int aOctave, double aCents )
    {
        note   = aNote;
        octave = aOctave;
        cents  = aCents;
    }
}
