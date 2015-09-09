package nl.igorski.lib.framework.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public interface IAsyncCommand extends ICommand
{
    public void setCancelHandler(IAsyncCommandHandler handler);
}
