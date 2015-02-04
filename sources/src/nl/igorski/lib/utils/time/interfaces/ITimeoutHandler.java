package nl.igorski.lib.utils.time.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-05-12
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 *
 * ITimeoutHandler should be implemented by callback functions
 * to be executed when a Timeout has completed
 */
public interface ITimeoutHandler
{
    public void onComplete();
}
