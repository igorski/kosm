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

import android.view.MotionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/12/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 *
 * IInteractiveSprite describes an IAnimatedSprite drawable that
 * can respond to interaction (touch) events
 */
public interface IInteractiveSprite extends IAnimatedSprite
{
    /**
     * to be called when the MotionEvent.ACTION_MOVE is being fired
     *
     * @param x {Float} current event position
     * @param y {Float} current event position
     * @param ev {MotionEvent} can be passed in case the InteractiveAnimation
     *           needs to read multi touch gestures
     */
    public void updatePosition ( Float x, Float y, MotionEvent ev );
    public boolean handleTouchDown( Float x, Float y, MotionEvent ev );
    public boolean handleTouchUp  ( MotionEvent ev );

    /* multi touch events (boolean values indicate whether they were handled) */

    public boolean handlePointerDown( MotionEvent aEvent );
    public boolean handlePointerUp  ( MotionEvent aEvent );

    public boolean isInteractive();
}
