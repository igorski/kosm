package nl.igorski.lib.audio.oscillators;

import nl.igorski.lib.audio.renderer.NativeAudioRenderer;
import nl.igorski.lib.audio.definitions.WaveForms;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 20-04-12
 * Time: 10:25
 * To change this template use File | Settings | File Templates.
 */
public class LFO
{
    protected double TWO_PI_OVER_SR;
    protected double TWO_PI;

    protected double _phase     = 0.0;
    protected double _phaseIncr = 0.0;
    protected double _rate;
    protected int _wave;

    // cached buffer
    public double[] buffer;

    public int length;

    /**
     * creates a Low Frequency Oscillator
     * @param aWave   {int} constant from the WaveForms class, (currently) only
     *                SINE_WAVE, TRIANGLE, SAWTOOTH and SQUARE_WAVE are available
     * @param aRate   {double} the oscillation rate/frequency in Hz
     * @param aLength {int} the length of the oscillators buffer
     */
    public LFO( int aWave, double aRate, int aLength )
    {
        TWO_PI          = 2 * Math.PI;
        TWO_PI_OVER_SR  = TWO_PI / NativeAudioRenderer.SAMPLE_RATE;

        _wave   = aWave;
        length  = aLength;

        setRate( aRate );
    }

    /* public */

    public void generate( int aLength )
    {
        length = aLength;
        buffer = new double[ aLength ];

        // we improve the use of the CPU resources
        // by creating a small local thread
        new Thread(
        new Runnable()
        {
            public void run()
            {
                double output = 0;
                double tmp;

                for ( int i = 0; i < length; ++i )
                {
                    switch( _wave )
                    {
                        case WaveForms.SINE_WAVE:

                            if ( _phase < .5 )
                            {
                                tmp = ( _phase * 4.0 - 1.0 );
                                output = ( 1.0 - tmp * tmp );
                            }
                            else {
                                tmp = ( _phase * 4.0 - 3.0 );
                                output = ( tmp * tmp - 1.0 );
                            }

                            break;

                        case WaveForms.TRIANGLE:

                            output = ( _phase - ( int )( _phase )) * 4;

                            if ( output < 2 )
                                output -= 1;
                            else
                                output = 3 - output;
                            break;

                        case WaveForms.SQUARE_WAVE:

                            if ( _phase < .5 )
                            {
                                tmp = TWO_PI * ( _phase * 4.0 - 1.0 );
                                output = ( 1.0 - tmp * tmp );
                            }
                            else {
                                tmp = TWO_PI * ( _phase * 4.0 - 3.0 );
                                output = ( tmp * tmp - 1.0 );
                            }
                            break;

                        case WaveForms.SAWTOOTH:

                            output = ( _phase < 0 ) ? _phase - ( int )( _phase - 1 ) : _phase - ( int )( _phase );
                            break;
                    }
                    _phase += _phaseIncr;

                    // restore _phase, max range is 0 - 1 ( double )
                    if ( _phase >= 1 )
                        _phase = 0;

                    buffer[ i ] = output;
                }
            }
        }).start();
    }

    public JSONObject getData()
    {
        JSONObject data = new JSONObject();

        try
        {
            data.put( "rate", _rate );
            data.put( "wave", _wave );
        }
        catch ( JSONException e ) {}

        return data;
    }

    public double getRate()
    {
        return _rate;
    }

    public void setRate( double value )
    {
        _rate      = value;
        _phase     = 0.0;
        _phaseIncr = value / NativeAudioRenderer.SAMPLE_RATE;

        generate( length );
    }

    public int getWave()
    {
        return _wave;
    }

    public void setWave( int value )
    {
        _wave = value;

        generate( length );
    }
}
