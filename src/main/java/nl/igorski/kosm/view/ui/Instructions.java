package nl.igorski.kosm.view.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import nl.igorski.lib.ui.components.MultilineText;
import nl.igorski.kosm.definitions.Assets;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.ui.base.InteractiveSprite;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 20-01-15
 * Time: 21:13
 * To change this template use File | Settings | File Templates.
 */
public final class Instructions extends InteractiveSprite
{
    private MultilineText _bodyText;
    private Paint         _blind;
    private String        _title;
    private TextPaint     _titlePaint;
    private TextPaint     _bodyPaint;
    private Rect          _titleBounds;

    public Instructions( int aWidth, int aHeight )
    {
        super( null, "INSTRUCTIONS", aWidth, aHeight, 60, 1 );

        _blind = new Paint();
        _blind.setColor( Color.BLACK );
        _blind.setAlpha( 85 );

        _titlePaint = new TextPaint();
        _titlePaint.setAntiAlias( true );
        _titlePaint.setTypeface ( Assets.DEFAULT_FONT );
        _titlePaint.setTextSize ( Assets.TEXT_SIZE_HEADER );
        _titlePaint.setColor    ( Assets.COLOR_KOSM_BLUE );

        _bodyPaint = new TextPaint();
        _bodyPaint.setAntiAlias( true );
        _bodyPaint.setTypeface ( Assets.DEFAULT_FONT );
        _bodyPaint.setTextSize ( Assets.TEXT_SIZE_NORMAL );
        _bodyPaint.setColor    ( Color.WHITE );

        _bodyText   = new MultilineText( "", _bodyPaint, ( int ) ( aWidth * .7 ));
        _bodyText.setXPos( aWidth / 2 - ( int )( aWidth * .7 ) / 2 );

        _titleBounds = new Rect();
    }

    /* public methods */

    public void setContent( int aTitleResource, int aBodyResource )
    {
        _title = Core.getContext().getResources().getString( aTitleResource );
        _bodyText.setText( aBodyResource );

        //final int textWidth   = ( int ) _titlePaint.measureText( _title );
        _titlePaint.getTextBounds( _title, 0, _title.length(), _titleBounds );

        _bodyText.setYPos( spriteHeight / 2 - _bodyText.getCoords().height() / 2 );
        _titleBounds.top  = _bodyText.getYPos() - ( int )( _titleBounds.height() * 1.5 );
        _titleBounds.left = spriteWidth / 2 - _titleBounds.width() / 2;
    }

    @Override
    public void draw( Canvas c )
    {
        c.drawRect( 0, 0, spriteWidth, spriteHeight, _blind );
        c.drawText( _title, _titleBounds.left, _titleBounds.top, _titlePaint );
        _bodyText.draw( c );
    }
}
