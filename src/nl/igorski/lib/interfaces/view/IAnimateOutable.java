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
package nl.igorski.lib.interfaces.view;

import nl.igorski.lib.interfaces.IDestroyable;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 02-03-13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public interface IAnimateOutable extends IDestroyable
{
    /**
     * animate the current view out, upon completion of the animation
     * the "onViewRemoved"-method of the passed ICanvasViewSwitcher must
     * be invoked for removing the IAnimateOutable view off the render list
     *
     * @param aViewSwitcher
     */
    public void animateOut( ICanvasViewSwitcher aViewSwitcher );
}
