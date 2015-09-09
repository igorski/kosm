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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import nl.igorski.lib.ui.interfaces.IAnimatedSprite;

/**
 * AnimatedSprite is the core class for a visual component
 * that is rendered onto a Canvas. It caters for internal
 * update operations (see "fps" and "spriteSheet") and can
 * be overridden to create game objects such as players, bullets, etc.
 */
public class AnimatedSprite implements IAnimatedSprite
{
    protected Bitmap      spriteSheet;
    protected int         xPos;
    protected int         yPos;
    protected Rect        sRectangle;
    protected Rect        coords;
    protected int         fps;
    protected int         numFrames;
    protected int         currentFrame;
    protected long        frameTimer;
    protected int         spriteHeight;
    protected int         spriteWidth;
    protected boolean     loop;
    public boolean        dispose;

    protected String      _id;

    // we might want to tween properties relative to the time domain, we
    // need to know the frame-rate of renderer to calculate the incrementValue
    // to be applied on each render cycle, this incremental value can be used
    // for timed animations, the incremental value is for a single millisecond

    protected double _frameIncrement;

    public AnimatedSprite( Bitmap bitmap, String id, int width, int height, int fps, int frameCount, boolean aLoop )
    {
        spriteSheet        = bitmap;
        spriteHeight       = height;
        spriteWidth        = width;
        sRectangle         = new Rect( 0, 0, spriteWidth, spriteHeight );

        setFrameRate( fps );

        numFrames          = frameCount;
        loop               = aLoop;

        _id                = id;
        frameTimer         = 0;
        currentFrame       = 0;
        xPos               = 0;
        yPos               = 0;
        dispose            = false;

        // the coordinates are cached as creation of Objects during
        // render cycles is quite expensive and might cause the garbage collector to hit ;)

        coords = new Rect( xPos, yPos, xPos + spriteWidth, yPos + spriteHeight );
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }

    public void setXPos( int value )
    {
        xPos = value;
        cacheCoordinates();
    }

    public void setYPos( int value )
    {
        yPos = value;
        cacheCoordinates();
    }

    public int getSpriteWidth()
    {
        return spriteWidth;
    }

    public void setSpriteWidth( int value )
    {
        spriteWidth = value;
        cacheCoordinates();
    }

    public int getSpriteHeight()
    {
        return spriteHeight;
    }

    public void setSpriteHeight( int value )
    {
        spriteHeight = value;
        cacheCoordinates();
    }

    public int getAlpha()
    {
        // override in subclass
        return 100;
    }

    public void setAlpha( int aAlpha )
    {
        // override in subclass
    }

    public Rect getCoords()
    {
        return coords;
    }

    public String getId()
    {
        return _id;
    }

    public void setFrameRate( int aFrameRate )
    {
        fps             = 1000 / aFrameRate;
        _frameIncrement = 1 / ( double ) aFrameRate;
    }

    public void update( long renderTime )
    {
        if ( renderTime > frameTimer + fps )
        {
            frameTimer = renderTime;
            currentFrame += 1;

            if ( currentFrame >= numFrames )
            {
                currentFrame = 0;

                if ( !loop )
                    dispose = true;
            }
            sRectangle.left  = currentFrame * spriteWidth;
            sRectangle.right = sRectangle.left + spriteWidth;
        }
    }

    public void draw( Canvas canvas )
    {
        canvas.drawBitmap( spriteSheet, sRectangle, coords, null );
    }

    public boolean handlePointerDown( MotionEvent aEvent )
    {
        return false;
    }

    public boolean handlePointerUp( MotionEvent aEvent )
    {
        return false;
    }

    /* protected methods */

    /**
     * invoked whenever the sprites dimensions
     * or coordinates change
     */
    protected void cacheCoordinates()
    {
        coords.left   = xPos;
        coords.top    = yPos;
        coords.right  = xPos + spriteWidth;
        coords.bottom = yPos + spriteHeight;
    }
}
