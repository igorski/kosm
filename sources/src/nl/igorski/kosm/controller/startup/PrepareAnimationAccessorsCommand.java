package nl.igorski.kosm.controller.startup;

import android.widget.Button;
import android.widget.ImageButton;
import nl.igorski.lib.animation.Animator;
import nl.igorski.lib.framework.controller.BaseSimpleCommand;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.ui.tween.ButtonAccessor;
import nl.igorski.lib.ui.tween.ImageButtonAccessor;
import nl.igorski.lib.utils.debugging.DebugTool;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 04-11-14
 * Time: 20:33
 * To change this template use File | Settings | File Templates.
 */
public final class PrepareAnimationAccessorsCommand extends BaseSimpleCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "PREPARE ANIMATION ACCESSORS COMMAND" );

        Animator.init();    // initializes tweening engine

        Animator.registerAccessor( Button.class,      new ButtonAccessor() );
        Animator.registerAccessor( ImageButton.class, new ImageButtonAccessor() );

        super.execute( aNote ); // completes command
    }
}
