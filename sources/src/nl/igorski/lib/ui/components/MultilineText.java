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
package nl.igorski.lib.ui.components;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.ui.base.InteractiveSprite;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 20-06-13
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 *
 * A component that renders multi line text onto a Canvas
 */
public class MultilineText extends InteractiveSprite
{
    private String       _text;
    private TextPaint    _textPaint;
    private StaticLayout _textLayout;

    public MultilineText( String aText, TextPaint aTextPaint, int aMaxWidth )
    {
        super( null, "MLT", aMaxWidth, 0, 30, 1 );

        _text = aText;

        init( aTextPaint, aMaxWidth );
    }

    public MultilineText( int aTextResource, TextPaint aTextPaint, int aMaxWidth )
    {
        this( Core.getContext().getResources().getString( aTextResource ), aTextPaint, aMaxWidth );
    }

    /* public methods */

    public void setText( String aText )
    {
        _text = aText;
        init( _textPaint, spriteWidth );
    }

    public void setText( int aTextResource )
    {
        setText( Core.getContext().getResources().getString( aTextResource ));
    }

    @Override
    public Rect getCoords()
    {
        return new Rect( getXPos(), getYPos(), getXPos() + spriteWidth, getYPos() + _textLayout.getHeight() );
    }

    public void draw( Canvas canvas )
    {
        canvas.save();

        canvas.translate( getXPos(), getYPos() );
        _textLayout.draw( canvas );

        canvas.restore();
    }

    /* private methods */

    private void init( TextPaint aTextPaint, int aMaxWidth )
    {
        _textPaint  = aTextPaint;
        _textLayout = new StaticLayout( _text, _textPaint, aMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false );
    }
}
