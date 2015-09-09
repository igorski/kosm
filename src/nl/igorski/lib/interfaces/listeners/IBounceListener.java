package nl.igorski.lib.interfaces.listeners;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 22-04-12
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public interface IBounceListener
{
    /**
     * invoked after offline audio bouncing has completed
     * @param {boolean} completed
     */
    public abstract void handleBounceComplete( boolean completed );
}
