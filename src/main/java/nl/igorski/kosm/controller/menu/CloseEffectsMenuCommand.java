package nl.igorski.kosm.controller.menu;

import android.widget.ImageButton;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import nl.igorski.kosm.Kosm;
import nl.igorski.lib.animation.Animator;
import nl.igorski.lib.animation.enums.AnimationProps;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.ui.ButtonTool;

import nl.igorski.kosm.R;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 19-01-15
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public final class CloseEffectsMenuCommand extends OpenWaveformMenuCommand
{
    public static boolean animationLocked = false; // only one animation instance at a time!

    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log("CLOSE EFFECTS MENU COMMAND");

        // prevent other animations
        if ( animationLocked ) {
            commandComplete();
            return;
        }

        animationLocked = true;

        // set toggle button as inactive, and animate out
        ButtonTool.setImageButtonImage(Kosm.btnEffectsToggle, R.drawable.toggle_fx);

        Animator.execute(Tween.to(Kosm.btnEffectsToggle, AnimationProps.POSITION_Y, .5f).
                target(-getButtonBottomCoordinate(Kosm.btnEffectsToggle)).
                ease(Cubic.IN)).setCallback( new toggleAnimationComplete() );
    }

    /* event handlers */

    private class toggleAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, BaseTween<?> baseTween )
        {
            final ImageButton[] buttons = new ImageButton[]{ Kosm.btnDelay, Kosm.btnDistortion, Kosm.btnFilter };

            int animatedButtons = -1;

            for ( final ImageButton button : buttons )
            {
                button.setClickable( false );

                final Tween t = Tween.to( button, AnimationProps.POSITION_X, .7f ).
                                          target( getButtonRightCoordinate( button )).ease( Cubic.OUT );

                t.delay( ++animatedButtons * .1f );

                Animator.execute( t );
            }

            // slide toggle button back in

            Animator.execute( Tween.to( Kosm.btnEffectsToggle, AnimationProps.POSITION_Y, .7f ).
                                        target( 0 )).ease( Cubic.OUT ).delay( animatedButtons * .1f ).
                                        setCallback( new menuAnimationComplete() );
        }
    }

    private class menuAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, BaseTween<?> baseTween )
        {
            DebugTool.log( "MENU ANI COMPLETE" );
            animationLocked         = false;
            Kosm.effectsMenuOpened  = false;
            commandComplete();
        }
    }
}
