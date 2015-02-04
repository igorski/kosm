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

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 02-03-13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public interface ICanvasViewSwitcher
{
    /**
     * invoked by a view implementing the IAnimateOutable interface
     * to notify the ICanvasViewSwitcher the view has finished its
     * animateOut-sequence and can be removed
     *
     * if there is a new view queued in the CanvasViewManager the
     * completeSwitch-method should be invoked to add the queued view
     *
     * @param aRemovedView {IAnimateOutable} the view which has
     *        finished its animateOut-sequence
     */
    public void onViewRemoved( IAnimateOutable aRemovedView );
}
