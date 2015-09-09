/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 Igor Zinken - http://www.igorski.nl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package nl.igorski.lib.ui.base;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import nl.igorski.lib.ui.interfaces.IInteractiveSprite;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/11/12
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 *
 * An extension to AnimatedSprite that can listen and respond to touch events
 * you can extend this class to create Objects like buttons or players
 */
public class InteractiveSprite extends AnimatedSprite implements IInteractiveSprite
{
    protected boolean _isDragging = false;
    protected long _dragStartTime;
    protected int _dragStartX;
    protected int _dragStartY;

    protected boolean _interactive = true; // whether we can receive touch events

    public InteractiveSprite( Bitmap bitmap, String id, int width, int height, int fps, int frameCount )
    {
        super( bitmap, id, width, height, fps, frameCount, true );
    }

    /* public methods */

    public boolean isInteractive()
    {
        return _interactive;
    }

    public boolean handleTouchDown( Float x, Float y, MotionEvent ev )
    {
        _dragStartX = Math.round( x );
        _dragStartY = Math.round( y );

        _dragStartTime = System.currentTimeMillis();

        _isDragging = true;

        return true;
    }

    public boolean handleTouchUp( MotionEvent ev )
    {
        _isDragging = false;

        return true;
    }

    public void updatePosition( Float x, Float y, MotionEvent ev )
    {
        /**
         * position is updated during drag move, we
         * calculate the new position relative to the
         * original position
         */
        xPos = Math.round( _dragStartX + x - ( spriteWidth / 2 ));
        yPos = Math.round( _dragStartY + y - ( spriteHeight / 2 ));
    }
}
