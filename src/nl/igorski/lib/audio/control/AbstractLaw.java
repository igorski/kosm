package nl.igorski.lib.audio.control;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 10/12/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractLaw
{
    protected static final int resolution = 1024;
    protected double min;
    protected double max;
    protected String units;

    protected AbstractLaw( double min, double max, String units )
    {
        this.min = min;
        this.max = max;
        this.units = units;
    }

    public int getResolution() { return resolution; }

    public double getMinimum() { return min; }
    public double getMaximum() { return max; }
    public String getUnits  () { return units; }
}
