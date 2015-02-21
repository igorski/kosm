package nl.igorski.lib.ui.tween;

import android.widget.ImageButton;
import aurelienribon.tweenengine.TweenAccessor;
import nl.igorski.lib.animation.enums.AnimationProps;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 01-03-13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class ImageButtonAccessor implements TweenAccessor<ImageButton>
{
    /* public methods */

    public int getValues( ImageButton target, int tweenType, float[] returnValues )
    {
        // always return the amount of values available in "returnValues"

        switch( tweenType )
        {
            case AnimationProps.POSITION_X:
                returnValues[ 0 ] = ViewHelper.getTranslationX(target);
                return 1;

            case AnimationProps.POSITION_Y:
                returnValues[ 0 ] = ViewHelper.getTranslationY(target);
                return 1;

            case AnimationProps.POSITION_XY:
                returnValues[ 0 ] = ViewHelper.getTranslationX(target);
                returnValues[ 1 ] = ViewHelper.getTranslationY(target);
                return 2;

            case AnimationProps.ALPHA:
                returnValues[ 0 ] = ViewHelper.getAlpha( target );
                return 1;

            case AnimationProps.HEIGHT:
                returnValues[ 0 ] = ( float ) target.getHeight();
                break;

            case AnimationProps.WIDTH:
                returnValues[ 0 ] = ( float ) target.getWidth();
                break;

            case AnimationProps.POSITION_X_WIDTH:
                returnValues[ 0 ] = ViewHelper.getTranslationX( target );
                returnValues[ 1 ] = ( float ) target.getWidth();
                break;

            default:
                assert false;
                return -1;
        }
        return -1;
    }

    public void setValues( ImageButton target, int tweenType, float[] newValues )
    {
        switch( tweenType )
        {
            case AnimationProps.POSITION_X:
                ViewHelper.setTranslationX( target, newValues[0]);
                break;

            case AnimationProps.POSITION_Y:
                ViewHelper.setTranslationY( target, newValues[0]);
                break;

            case AnimationProps.POSITION_XY:
                ViewHelper.setTranslationX( target, newValues[0]);
                ViewHelper.setTranslationY( target, newValues[1]);
                break;

            case AnimationProps.ALPHA:
                ViewHelper.setAlpha( target, newValues[ 0 ]);
                break;

            case AnimationProps.WIDTH:
                target.setMinimumWidth(( int ) newValues[ 0 ]);
                break;

            case AnimationProps.HEIGHT:
                target.setMinimumHeight(( int ) newValues[ 0 ]);
                break;

            case AnimationProps.POSITION_X_WIDTH:
                ViewHelper.setTranslationX( target, newValues[ 0 ]);
                target.setMinimumWidth(( int ) newValues[ 1 ]);
                break;

            default:
                assert false;
                break;
        }
    }
}
