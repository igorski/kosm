package nl.igorski.lib.ui.tween;

import android.widget.Button;
import aurelienribon.tweenengine.TweenAccessor;
import nl.igorski.lib.animation.enums.AnimationProps;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 01-03-13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class ButtonAccessor implements TweenAccessor<Button>
{
    /* public methods */

    public int getValues( Button target, int tweenType, float[] returnValues )
    {
        // always return the amount of values available in "returnValues"

        switch( tweenType )
        {
            case AnimationProps.POSITION_X:
                returnValues[ 0 ] = target.getTranslationX();
                return 1;

            case AnimationProps.POSITION_Y:
                returnValues[ 0 ] = target.getTranslationY();
                return 1;

            case AnimationProps.POSITION_XY:
                returnValues[ 0 ] = target.getTranslationX();
                returnValues[ 1 ] = target.getTranslationY();
                return 2;

            case AnimationProps.ALPHA:
                returnValues[ 0 ] = ( float ) target.getAlpha();
                return 1;

            case AnimationProps.HEIGHT:
                returnValues[ 0 ] = ( float ) target.getHeight();
                break;

            case AnimationProps.WIDTH:
                returnValues[ 0 ] = ( float ) target.getWidth();
                break;

            case AnimationProps.POSITION_X_WIDTH:
                returnValues[ 0 ] = target.getTranslationX();
                returnValues[ 1 ] = ( float ) target.getWidth();
                break;

            default:
                assert false;
                return -1;
        }
        return -1;
    }

    public void setValues( Button target, int tweenType, float[] newValues )
    {
        switch( tweenType )
        {
            case AnimationProps.POSITION_X:
                target.setTranslationX( newValues[ 0 ] );
                break;

            case AnimationProps.POSITION_Y:
                target.setTranslationY( newValues[ 0 ] );
                break;

            case AnimationProps.POSITION_XY:
                target.setTranslationX( newValues[ 0 ] );
                target.setTranslationY( newValues[ 1 ] );
                break;

            case AnimationProps.ALPHA:
                target.setAlpha(( int ) newValues[ 0 ]);
                break;

            case AnimationProps.WIDTH:
                target.setMinimumWidth(( int ) newValues[ 0 ] );
                break;

            case AnimationProps.HEIGHT:
                target.setMinimumHeight(( int ) newValues[ 0 ] );
                break;

            case AnimationProps.POSITION_X_WIDTH:
                target.setTranslationX( newValues[ 0 ] );
                target.setMinimumWidth(( int ) newValues[ 1 ] );
                break;

            default:
                assert false;
                break;
        }
    }
}
