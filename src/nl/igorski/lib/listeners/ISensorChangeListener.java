package nl.igorski.lib.listeners;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/27/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISensorChangeListener
{
    public abstract void handleChange( double aXvalue, double aYvalue, double aZvalue );
}
