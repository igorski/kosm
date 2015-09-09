package nl.igorski.kosm.view.physics.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import blissfulthinking.java.android.ape.AbstractParticle;
import blissfulthinking.java.android.ape.CircleParticle;
import blissfulthinking.java.android.ape.Vector;
import blissfulthinking.java.android.apeforandroid.FP;
import nl.igorski.kosm.model.KosmInstruments;
import nl.igorski.kosm.util.EventDisposalUtil;
import nl.igorski.kosm.view.ParticleSequencer;
import nl.igorski.lib.audio.MWEngine;
import nl.igorski.lib.audio.definitions.WaveForms;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
import nl.igorski.lib.utils.math.MathTool;
import nl.igorski.lib.utils.time.Timeout;
import blissfulthinking.java.android.ape.APEngine;

import nl.igorski.lib.audio.nativeaudio.AudioParticleEvent;
import nl.igorski.lib.audio.nativeaudio.BaseAudioEvent;

import nl.igorski.kosm.definitions.ParticleSounds;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 28-06-12
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public final class AudioParticle extends CircleParticle
{
    /* orb properties (overridden by Assets.java for device-specific dimensions)  */

    public static double MIN_RADIUS  = 5;
    public static double MAX_RADIUS  = 25;
    public static float  STROKE_SIZE = 2;

    /* public */

    public boolean emitter = false;

    /* internal */

    private BaseAudioEvent    _event;
    private float             _frequency;
    private ParticleSequencer _container;

    private boolean _hasEvent;    // whether we can submit a new audio event to the sequencer
    private float[] _prevCollision;
    private float   _minCollisionDelta; // the amount of px in difference we assume as a new collision

    private ParticleSounds _particleSound;
    private Paint          _paint;
    private float          _radius;
    private float          _curRadius;
    private float          _maxRadius;

    private boolean growing;

    /**
     * @param particleSound the sound to render
     * @param container the ParticleSequencer class creating the particle
     * @param x The initial x position of this particle.
     * @param y The initial y position of this particle.
     * @param aRadius The radius of this particle.
     */
    public AudioParticle( ParticleSounds particleSound, ParticleSequencer container,
                          float x, float y, float aRadius, boolean isFixed )
    {
        // arg 4 and onwards : fixed, mass, elasticity, friction
        super( x, y, aRadius, isFixed, 0.5f * ( float )( aRadius / MIN_RADIUS ), 2f, 0.50f );

        _particleSound = particleSound;
        _container     = container;

        _minCollisionDelta = ParticleSequencer.width / 12;

        _paint = new Paint();
        _paint.setStrokeWidth( STROKE_SIZE );
        setMainColor();

        // cache these calculations
        _radius = FP.toFloat(radius);

        // only used for fixed particles
        if ( fixed )
        {
            _curRadius = _radius;
            _maxRadius = _radius * 1.1f;
            growing    = true;

            _paint.setAntiAlias( true );
        }

        // calculate audio frequency by radius
        final int maxFreq = 880; // in Hz
        final int minFreq = 40;

        _frequency = ( float ) Math.abs(( maxFreq - minFreq ) - MathTool.scale(aRadius, MAX_RADIUS, 880));
    }

    /* public methods */

    public void clearEvent()
    {
        _hasEvent = false;

        EventDisposalUtil.disposeEvent(_event); // prevents instant C++ destructor when object finalizes

        _event = null;                            // breaks reference in this class

        setMainColor();
    }

    public float getXpos()
    {
        return FP.toFloat( curr[ 0 ]);
    }

    public float getYpos()
    {
        return FP.toFloat( curr[ 1 ]);
    }

    @Override
    public final void drawParticle( Canvas c )
    {
        if ( !fixed )
            c.drawCircle( FP.toFloat( curr[ 0 ]), FP.toFloat( curr[ 1 ]), _radius, _paint );
        else
        {
            c.drawCircle( FP.toFloat( curr[ 0 ]), FP.toFloat( curr[ 1 ]), _curRadius, _paint );

            if ( growing )
            {
                _curRadius *= 1.003f;

                // invert direction
                if ( _curRadius > _maxRadius )
                    growing = false;
            }
            else
            {
                _curRadius /= 1.0015f;

                // invert direction
                if ( _curRadius < _radius )
                    growing = true;
            }
        }
    }

    public void destroy()
    {
        if ( _event != null )
            clearEvent();

        _container.removeAudioParticle( this );

        AbstractParticle currentParticle = APEngine.particles;
        int amount = 0;

        while ( currentParticle != null )
        {
            if ( currentParticle.next != null && currentParticle.next == this )
            {
                // currentParticle is this particles previous particle
                remove( this, currentParticle );
                return;
            }
            currentParticle = currentParticle.next;
            ++amount;
        }

        // no previous particle found ? perhaps this is the only one...
        if ( amount == 1 )
            APEngine.particles = null;

        // TODO: something is still wrong, particles are still drawn ( thus present in APE... )
        _paint.setARGB( 0, 0, 0, 0 );
    }

    @Override
    public void resolveCollision( int[] mtd, int[] vel, int[] n, int d, int o )
    {
        Vector.supply_plus(curr, mtd, curr);

        // no switching of collisionResponseMode on the APEngine, force SELECTIVE
        if ( !isColliding )
            setVelocity( vel );

        boolean doCollide = false;
        final float curX  = getXpos();
        final float curY  = getYpos();

//        final float xVel = FastMath.abs( FP.toFloat( vel[ 0 ]));
//        final float yVel = FastMath.abs( FP.toFloat( vel[ 1 ]));
//
//        if ( xVel > 1 || yVel > 1 )
//        {
//            // do bounce!
//            // briefly invert gravity perhaps ?
//            //Log.d( "SYNTH", "COLLIDE");
//        }
        if ( _prevCollision == null )
        {
            doCollide = true;
            _prevCollision = new float[]{ curX, curY };
        }
        else
        {
            // if we have previously collided, we make sure
            // that we have moved a minimum value in px before
            // we re-trigger the collision event - we want to know
            // the moment of impact, not whether we're resting on another object

            final float prevX = _prevCollision[ 0 ];
            final float prevY = _prevCollision[ 1 ];

            if ( abs(curX - prevX) > _minCollisionDelta ||
                 abs(curY - prevY) > _minCollisionDelta )
            {
                doCollide = true;
                //DebugTool.log( "recollide" );
                _prevCollision[ 0 ] = curX;
                _prevCollision[ 1 ] = curY;
            }
        }

        if ( doCollide || fixed ) // note that fixed particles always resolve the collision
        {
            isColliding = true;

            if ( !_hasEvent )
                createAudioEvent();
            else {
                (( AudioParticleEvent ) _event ).reset(); // reset for re-use
                _event.setDeletable( true );              // ensures it can be removed again
                setHighlightColor();
            }
        }
    }

    @Override
    public void remove( AbstractParticle p, AbstractParticle previous )
    {
        if ( p == this )
        {
            if ( previous != null )
                previous.next = next;
        }
        else if ( next != null )
        {
            next.remove( p, this );
        }
    }

    public boolean handleTouchDown( Float x, Float y )
    {
        final float theRadius = FP.toFloat( radius );
        final float theX      = getXpos();
        final float theY      = getYpos();

       // DebugTool.log( "TEST => "  + x + " x "  + y + " : "  + centerX + " x "  + centerY + " + "  + theRadius);
        return ( x >= ( theX - theRadius )  && x <= ( theX + theRadius )
                    && y >= ( theY - theRadius ) && y <= ( theY + theRadius ));
    }

    /* private methods */

    private void createAudioEvent()
    {
        final InternalSynthInstrument instrumentProperties = KosmInstruments.getInstrumentByParticleSound(_particleSound);
        final int waveform = WaveForms.waveformByParticleSound(_particleSound);

        instrumentProperties.instrument.setWaveform( waveform );

        int length    = 0;
        float attack  = 0;
        float release = 0;

        if ( waveform == WaveForms.SAWTOOTH ||
             waveform == WaveForms.SINE_WAVE )
        {
            length = MWEngine.BYTES_PER_BAR;// * 2;

            if ( _frequency > 600 )
                attack = .25f;
            else
                attack = .01f;

            // sines can cause annoying popping, make sure we have a nice release envelope
            if ( waveform == WaveForms.SINE_WAVE )
                release = .1f;
        }
        else if ( waveform == WaveForms.SQUARE_WAVE )
        {
            length = MWEngine.BYTES_PER_BAR;
        }

        if ( _event == null )
            _event = new AudioParticleEvent( _frequency, instrumentProperties.instrument, length, attack, release );

        _event.setDeletable( true ); // queued for deletion when synthesis completed minimum length

        _hasEvent = true;

        setHighlightColor();
    }

    private void setMainColor()
    {
        _paint.setARGB(255, 59, 179, 181);

        if ( fixed )
            _paint.setStyle( Paint.Style.STROKE );
    }

    private void setHighlightColor()
    {
        _paint.setARGB ( 255, 255, 255, 255 );
        _paint.setStyle(Paint.Style.FILL);

        if ( fixed )
        {
            Timeout.setTimeout(350, new Runnable() {
                @Override
                public void run() {
                    setMainColor();
                }
            }, true);
        }
    }

    // provide a faster abs, no NaN handling
    private static float abs( final float x )
    {
        return x < 0 ? -x : x;
    }
}
