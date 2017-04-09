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
public final class CloseWaveformMenuCommand extends OpenWaveformMenuCommand
{
    @Override
    public void execute( INotification aNote )
    {
        DebugTool.log("CLOSE WAVEFORM MENU COMMAND");

        // prevent other animations
        if ( wfAnimationLocked ) {
            commandComplete();
            return;
        }

        wfAnimationLocked = true;

        // set toggle button as inactive, and animate out
        ButtonTool.setImageButtonImage(Kosm.btnWaveformToggle, R.drawable.toggle_wf);

        Animator.execute(Tween.to(Kosm.btnWaveformToggle, AnimationProps.POSITION_Y, .5f).
                target(-getButtonBottomCoordinate(Kosm.btnWaveformToggle)).
                ease(Cubic.IN)).setCallback( new toggleAnimationComplete() );
    }

    /* event handlers */

    private class toggleAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, BaseTween<?> baseTween )
        {
            final ImageButton[] buttons = new ImageButton[]{ Kosm.btnSine, Kosm.btnSaw, Kosm.btnTwang,
                                                             Kosm.btnKick, Kosm.btnSnare };

            int animatedButtons = -1;

            for ( final ImageButton button : buttons )
            {
                button.setClickable( false );

                final Tween t = Tween.to( button, AnimationProps.POSITION_X, .7f ).
                                          target(  -getButtonRightCoordinate(button)).ease( Cubic.OUT );

                t.delay( ++animatedButtons * .1f );

                Animator.execute( t );
            }

            // slide toggle button back in

            Animator.execute( Tween.to( Kosm.btnWaveformToggle, AnimationProps.POSITION_Y, .7f ).
                                        target( 0 )).ease( Cubic.OUT ).delay( animatedButtons * .1f ).
                                        setCallback( new menuAnimationComplete() );
        }
    }

    private class menuAnimationComplete implements TweenCallback
    {
        public void onEvent( int i, aurelienribon.tweenengine.BaseTween<?> baseTween )
        {
            DebugTool.log( "WAVEFORM MENU ANI COMPLETE" );
            wfAnimationLocked       = false;
            Kosm.waveformMenuOpened = false;
            commandComplete();
        }
    }
}
