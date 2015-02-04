package nl.igorski.lib.framework.interfaces;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public interface ICommand
{
    /**
     * commands are fired by the Core invoking the execute method
     *
     * @param {INotification} notification
     */
    public void execute(INotification aNotification);

    /**
     * when commands are part of a chain (MacroCommand), a
     * complete handler is passed to inform the MacroCommand
     * the next command in the queue can be executed
     *
     * @param handler {ICommandHandler}
     */
    public void setCompleteHandler(ICommandHandler handler);
}
