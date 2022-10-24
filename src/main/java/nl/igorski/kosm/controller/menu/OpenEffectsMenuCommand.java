package nl.igorski.kosm.controller.menu;

import android.widget.ImageButton;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import nl.igorski.lib.animation.enums.AnimationProps;

import nl.igorski.kosm.R;
import nl.igorski.kosm.Kosm;
import nl.igorski.lib.animation.Animator;
import nl.igorski.lib.framework.interfaces.INotification;
import nl.igorski.lib.utils.debugging.DebugTool;
import nl.igorski.lib.utils.ui.ButtonTool;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 19-01-15
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public class OpenEffectsMenuCommand extends AnimatedMenuCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log( "OPEN EFFECTS MENU COMMAND" );

        // prevent other animations
        if ( fxAnimationLocked ) {
            commandComplete();
            return;
        }

        fxAnimationLocked = true;

        ImageButton btnEffectsToggle = Kosm.getBtnEffectsToggle();

        // set toggle button as inactive, and animate out
        ButtonTool.setImageButtonImage( btnEffectsToggle, R.drawable.toggle_fx_active );

        Animator.execute(
            Tween.to( btnEffectsToggle, AnimationProps.POSITION_Y, .5f ).
            target( -getButtonBottomCoordinate( btnEffectsToggle )).
            ease( Cubic.IN )
        ).setCallback( new toggleAnimationComplete() );
    }

    /* event handlers */

    private class toggleAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, BaseTween<?> baseTween )
        {
            final ImageButton[] buttons = new ImageButton[]{
                Kosm.getBtnDelay(), Kosm.getBtnDistortion(), Kosm.getBtnFilter()
            };
            int animatedButtons = -1;

            for ( final ImageButton button : buttons )
            {
                button.setTranslationX( getButtonRightCoordinate( button ));
                button.setAlpha    ( 1f );
                button.setClickable( true );

                final Tween t = Tween.to( button, AnimationProps.POSITION_X, .7f ).
                                          target( 0f ).ease( Cubic.OUT );

                t.delay( ++animatedButtons * .1f );

                Animator.execute( t );
            }

            // slide toggle button back in

            Animator.execute(
                Tween.to( Kosm.getBtnEffectsToggle(), AnimationProps.POSITION_Y, .7f ).
                target( 0 )
            ).ease( Cubic.OUT ).delay( animatedButtons * .1f )
            .setCallback( new menuAnimationComplete() );
        }
    }

    private class menuAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, BaseTween<?> baseTween )
        {
            DebugTool.log( "FX MENU ANI COMPLETE" );
            fxAnimationLocked      = false;
            Kosm.effectsMenuOpened = true;
            commandComplete();
        }
    }

    /* private methods */

    protected int getButtonRightCoordinate( ImageButton button )
    {
        return button.getWidth() + button.getLeft();
    }

    protected int getButtonBottomCoordinate( ImageButton button )
    {
        return button.getHeight() + button.getTop();
    }
}
