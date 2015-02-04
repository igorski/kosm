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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import nl.igorski.lib.interfaces.listeners.IChangeListener;
import nl.igorski.lib.ui.base.InteractiveSprite;
import nl.igorski.lib.utils.math.MathTool;
import nl.igorski.lib.utils.time.Timeout;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/18/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 *
 * A "rotary dial" view component that renders itself onto a Canvas
 */
public class Rotary extends InteractiveSprite
{
    private static double MAX_ROTATION  = 270.0;
    private static double MAX_ROT_DELTA = 360.0 - MAX_ROTATION; // the difference between a full circle
                                                                // and the full allowed travel
    private double TWO_PI;

    /* abstract value related */

    protected double _min;
    protected double _max;

    protected boolean _enabled;
    protected boolean _roundedValues;
    protected double _lastValue;

    /* UI behaviour related */

    private boolean _broadcastOnRelease = false;
    protected int _updateDelay;
    protected boolean _hasDelay;
    private boolean _showValue;
    private Runnable _timeoutHandler;

    /* view related */

    protected double _rotation;
    protected double _size;
    protected double _ballRadius;
    protected float _pointerRadius = 5;
    protected int circleCenterX;
    protected int circleCenterY;
    protected DecimalFormat _df;
    protected int _backgroundColor;
    protected int _highlightColor;
    private double _tmpValue;

    /* outline */

    protected RectF _sliderRect;

    /* pooled Paint Objects */

    protected static Paint _textPaint;
    protected static Paint _commonPaint;
    protected static Paint _sliderPaint;

    /* event handlers */

    private IChangeListener _changeListener;

    //_________________________________________________________________________________________________________________
    //                                                                                            C O N S T R U C T O R

    /**
     * @param size          {double} the size of the Rotary element, this should be seen as circle radius
     * @param min           {double} the value corresponding to the Rotary's zero degree state
     * @param max           {double} the value corresponding to the Rotary's maximum degree state
     * @param defaultValue  {double} the initial value of the Rotary element
     * @param enabled       {boolean} whether the Rotary element is _interactive
     * @param roundedValues {boolean} returns rounded integers instead of floating point Numbers
     */
    public Rotary( double size, double min, double max, double defaultValue, boolean enabled, boolean roundedValues )
    {
        super( null, "RT::" + ( Math.random() * 100 ), ( int ) size, ( int ) size, 30, 1 );
        construct( size, min, max, defaultValue, enabled, roundedValues, 0 );
    }

    /**
     * @param size          {double} the size of the Rotary element, this should be seen as circle radius
     * @param min           {double} the value corresponding to the Rotary's zero degree state
     * @param max           {double} the value corresponding to the Rotary's maximum degree state
     * @param defaultValue  {double} the initial value of the Rotary element
     * @param enabled       {boolean} whether the Rotary element is _interactive
     * @param roundedValues {boolean} returns rounded integers instead of floating point Numbers
     * @param updateDelay   {int} optional, only broadcasts a change in value after the value of
     *                      updateDelay in milliseconds has passed, useful when a Rotary is triggering
     *                      a lot of CPU resources upon change
     */
    public Rotary( double size, double min, double max, double defaultValue, boolean enabled, boolean roundedValues, int updateDelay )
    {
        super( null, "RT::" + ( Math.random() * 100 ), ( int ) size, ( int ) size, 30, 1 );
        construct( size, min, max, defaultValue, enabled, roundedValues, updateDelay );
    }

    protected void construct( double size, double min, double max, double defaultValue, boolean enabled, boolean roundedValues, int updateDelay )
    {
        _showValue     = false;
        _size          = size * .5;  // full size reduced to circle radius
        _ballRadius    = _size * .6;
        _min           = min;
        _max           = max;
        _enabled       = enabled;
        _roundedValues = roundedValues;

        if ( defaultValue == 0 )
            defaultValue = _min;

        _lastValue   = defaultValue;
        _updateDelay = updateDelay;

        _timeoutHandler = new Runnable()
                          {
                              public void run()
                              {
                                  _hasDelay  = false;

                                  if ( _lastValue != _tmpValue )
                                  {
                                      if ( _changeListener != null )
                                          _changeListener.handleChange( _tmpValue );
                                  }
                                  _lastValue = _tmpValue;
                              }
                          };

        // prepare styles
        final int strokeSize = ( int ) ( _size * 0.12 );

        if ( _commonPaint == null )
        {
            _commonPaint = new Paint();
            _commonPaint.setStrokeWidth( strokeSize );
            _commonPaint.setAntiAlias( true );
        }
        _backgroundColor = Color.WHITE;

        if ( _textPaint == null )
        {
            _textPaint = new Paint();
            _textPaint.setColor( Color.BLACK );
            _textPaint.setAntiAlias( true );
            _textPaint.setTextSize ( 15 );
        }

        if ( _sliderPaint == null )
        {
            _sliderPaint = new Paint();
            _sliderPaint.setStrokeWidth( strokeSize );
            _sliderPaint.setAntiAlias( true );
            _sliderPaint.setStrokeCap( Paint.Cap.ROUND );
            _sliderPaint.setStyle    ( Paint.Style.STROKE );
        }

        _sliderRect = new RectF();
        setHighlightColor( Color.RED );

        _df = new DecimalFormat( "#.##" );

        TWO_PI = Math.PI * 2;

        setValue( defaultValue );
        setEnabled( _enabled );

        setXPos( 0 );
        setYPos( 0 );
    }

    /* public methods */

    @Override
    public void setXPos( int value )
    {
        super.setXPos( value );

        circleCenterX = xPos + ( int ) _size;
        calcSliderRect();
    }

    @Override
    public void setYPos( int value )
    {
        super.setYPos( value );

        circleCenterY = yPos + ( int ) _size;
        calcSliderRect();
    }

    public void setBackgroundColor( int aColor )
    {
        _backgroundColor = aColor;
    }

    public void setHighlightColor( int aColor )
    {
        _highlightColor = aColor;
        _sliderPaint.setColor( _highlightColor );
    }

    public void showValue( boolean show )
    {
        _showValue = show;
    }

    public boolean hasDelay()
    {
        return _hasDelay;
    }

    public void setDelay( int aDelay )
    {
        _updateDelay = aDelay;
    }

    /**
     * in certain situations it isn't feasible to broadcast an
     * update when changing values until the user has released
     * the rotary
     *
     * @param aValue {boolean}
     */
    public void broadcastOnlyOnRelease( boolean aValue )
    {
        _broadcastOnRelease = aValue;
    }

    // Allows the user to set an Listener and react to value changes

    public void setChangeListener( IChangeListener listener )
    {
        _changeListener = listener;
    }

    @Override
    public void draw( Canvas canvas )
    {
        // we multiply the percentage as we won t travel a full circle
        final double value = rotationToPercentage( _rotation ) * .82;

        double startAngle   = 0;
        double endAngle     = TWO_PI * value;

        final int dir       = 1;
        final double diff   = Math.abs( endAngle - startAngle );
        final double divs   = Math.floor( diff / ( Math.PI * .25 )) + 1;
        final double span   = dir * diff / ( 2 * divs );

        _commonPaint.setColor( _backgroundColor );

        // background first
        _commonPaint.setStyle( Paint.Style.FILL );
        canvas.drawCircle( circleCenterX, circleCenterY, ( float ) ( _size * .85 ), _commonPaint );

        // stroke around rotary
        _commonPaint.setStyle( Paint.Style.STROKE );
        _commonPaint.setColor( Color.BLACK );
        canvas.drawCircle( circleCenterX, circleCenterY, ( float ) ( _size * .85 ), _commonPaint );

        for ( int i = 0; i < divs; ++i )
        {
            endAngle   = startAngle + span;
            startAngle = endAngle   + span;
        }
        // draw the value as an outline
        canvas.drawArc( _sliderRect, 130, ( float )( endAngle * ( MAX_ROTATION * .22 )), false, _sliderPaint );

        // draw dot showing position
        final double ballAngle = TWO_PI * value * .97; /*( TWO_PI / _ballRadius ) * 360 * value;*/
        // we subtract a value at the end as we don't travel a full 360 degree arc
        final float ballXpos   = ( float ) ( circleCenterX + ( _ballRadius * Math.cos( ballAngle - 79.5 )));
        final float ballYpos   = ( float ) ( circleCenterY + ( _ballRadius * Math.sin( ballAngle - 79.5 )));

        _commonPaint.setColor( Color.BLACK );

        final float currentStroke = _commonPaint.getStrokeWidth();
        _commonPaint.setStrokeWidth( currentStroke / 2 );
        canvas.drawCircle( ballXpos, ballYpos, _pointerRadius, _commonPaint );
        _commonPaint.setStrokeWidth( currentStroke );

        if ( _showValue )
        {
            String output = _df.format( getValue());

            if ( output.length() == 1 )
                output += ".00";
            else
                output = output.replace( ",", "." );

            drawText( canvas, output, ( int ) ( _size * .5 ));
        }
    }

    public void destroy()
    {
        _changeListener = null;
    }

    /* getters / setters */

    public double getValue()
    {
        final double pct = rotationToPercentage( _rotation );
        final double dev = _max - _min;

        double theValue = ( pct * dev ) + _min;

        // keep value within range
        if ( theValue < _min )
            theValue = _min;

        else if ( theValue > _max )
            theValue = _max;

        return ( _roundedValues ) ? Math.round( theValue ) : theValue;
    }

    public void setValue( double aValue )
    {
        final double pct = ( aValue - _min ) / ( _max - _min );
        _rotation        = percentageToRotation( pct );

        if ( _changeListener != null )
            _changeListener.handleChange( getValue());
    }

    public double getMin()
    {
        return _min;
    }

    public void setMin( double aValue )
    {
        _min = aValue;
    }

    public double getMax()
    {
        return _max;
    }

    public void setMax( double aValue )
    {
        _max = aValue;
    }

    public boolean getEnabled()
    {
        return _enabled;
    }

    public void setEnabled( boolean aValue )
    {
        _enabled = aValue;
    }

    /* event handlers */

    @Override
    public boolean handleTouchUp( MotionEvent ev )
    {
        boolean handled = super.handleTouchUp( ev );
        updateValues( getValue());

        return handled;
    }

    @Override
    public void updatePosition( Float x, Float y, MotionEvent ev )
    {
        /**
         * a Rotary is a circle so:
         * positive _size = lowest offset value
         * negative _size = highest offset value
         */
        //final int center = ( int ) ( yPos + _size );

        if ( y >= -_size && y <= _size )
        {
            // the minimum value is -_size and the maximum value is _size
            // note that a y position with the value of the positive _size is the MINIMUM value of the rotary
            // and the y position with the value of the negative _size is the MAXIMUM value of the rotary

            _rotation = percentageToRotation( MathTool.scale( -y + _size, _size * 2, 1 ));

            if ( !_broadcastOnRelease )
                updateValues( getValue());
        }
    }

    /* protected methods */

    protected double rotationToPercentage( double aRotation )
    {
        if ( aRotation < 0 )
            aRotation = 180 + ( aRotation + 180 );

        return aRotation / ( MAX_ROTATION + ( MAX_ROT_DELTA * .5 ));
    }

    protected double percentageToRotation( double aValue )
    {
        return aValue * ( MAX_ROTATION + ( MAX_ROT_DELTA * .5 ));
    }

    protected void drawText( Canvas canvas, String text, int offsetY )
    {
        canvas.drawText( text, ( float ) ( xPos + ( _size * .5 )), ( float ) ( yPos + ( _size  * .5 )) + offsetY, _textPaint );
    }

    protected void calcSliderRect()
    {
        final float theRadius = ( float ) ( _size * .5 );

        // as a Rotary is a circle we use the radius for width and height calculations...
        _sliderRect.set( circleCenterX - theRadius - theRadius,
                         circleCenterY - theRadius - theRadius,
                         circleCenterX + theRadius + theRadius,
                         circleCenterY + theRadius + theRadius );
    }

    /* private methods */

    private void updateValues( double aValue )
    {
        // immediate broadcast of change ?

        if ( _updateDelay == 0 )
        {
            if ( _lastValue != aValue )
            {
                if ( _changeListener != null )
                    _changeListener.handleChange( aValue );
            }
            _lastValue = aValue;
        }
        else
        {
            // temporarily store the current value to
            // prevent differing results when the timeout has completed

            _tmpValue = aValue;

            // allow only one delayed broadcast at a time

            if ( _hasDelay )
                return;

            _hasDelay = true;

            Timeout.setTimeout( _updateDelay, _timeoutHandler, false );
        }
    }
}
