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
package nl.igorski.lib.ui.interfaces;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/11/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 *
 * An IAnimatedSprite describes an Object that can draw itself
 * onto a Canvas, can update its status (i.e. to draw different
 * frames of its optionally animated content) prior
 * to rendering is invoked and has getters and setters for
 * its coordinates and bounds
 */
public interface IAnimatedSprite
{
    /* state update and render logic */

    /**
     * update is invoked by the Renderer prior
     * to invoking an actual render request
     *
     * @param {long} timeStamp which can be
     *         used for internal update logic (for
     *         instance calculating delta time
     *         since last update)
     */
    public void update( long timeStamp );

    /**
     * invoked by the Renderer, this is
     * what actually renders the Sprites
     * contents onto a Canvas
     *
     * @param {Canvas} canvas
     */
    public void draw( Canvas canvas );

    /* coordinates */

    public int getXPos();
    public int getYPos();
    public void setXPos( int aValue );
    public void setYPos( int aValue );
    public Rect getCoords();

    public String getId();
}
