package nl.igorski.kosm.view.physics;

import java.util.ArrayList;

import android.graphics.Canvas;
import blissfulthinking.java.android.ape.APEngine;
import blissfulthinking.java.android.ape.AbstractConstraint;
import blissfulthinking.java.android.ape.AbstractParticle;
import blissfulthinking.java.android.ape.SpringConstraint;
import blissfulthinking.java.android.apeforandroid.FP;
import nl.igorski.lib.utils.math.MathTool;
import nl.igorski.kosm.view.ParticleSequencer;
import nl.igorski.kosm.view.physics.components.AudioParticle;
import nl.igorski.kosm.view.physics.components.ContainerParticle;

public final class PhysicsWorld
{
    private long usedTime;

    private final APEngine world = new APEngine();

    private final boolean DEBUG = false;

    public int previousRadius;

    private AbstractParticle[] containerSegments;

    // container related

    public boolean hasContainer;

    private int containerX;

    private int containerY;

    public final void updateWorld()
    {
        APEngine.step();

        AbstractParticle currentParticle = APEngine.particles;

        final int boundsRight  = ParticleSequencer.width  + 100;
        final int boundsBottom = ParticleSequencer.height + 100;

        // remove off-screen particles
        while ( currentParticle != null )
        {
            if ( currentParticle instanceof AudioParticle )
            {
                final AudioParticle p = ( AudioParticle ) currentParticle;

                final float theX = p.getXpos();
                final float theY = p.getYpos();

                if ( theX < -100 || theX > boundsRight )
                    p.destroy();

                else if ( theY < -100 || theY > boundsBottom )
                    p.destroy();
            }
            currentParticle = currentParticle.next;
        }
    }

    @SuppressWarnings("static-access")
    public void initWorld()
    {
        APEngine.particles = null;

        containerSegments = new AbstractParticle[ 64 ];

        previousRadius = 0;
        hasContainer   = false;

        // remove all existing constraints from the world
        ArrayList<AbstractConstraint> contraints = ( ArrayList<AbstractConstraint> ) APEngine.getAllConstraints().clone();
        for ( AbstractConstraint ac : contraints )
        {
            APEngine.removeConstraint( ac );
        }

        // set up the events, main loop handler, and the engine. you don't have to use
        // enterframe. you just need to call the ApeEngine.step() method wherever
        // and however you're handling your program cycle.

        // the argument here is the deltaTime value. Higher values result in faster simulations.
        APEngine.init( FP.fromDouble(( double ) 1 / 3 ));

        // SELECTIVE is better for dealing with lots of little particles colliding,
        APEngine.setCollisionResponseMode( APEngine.SELECTIVE );
    }

    public final void createContainer( final int aRadius, final int aX, final int aY )
    {
        final int segments = containerSegments.length;

        previousRadius = aRadius;

        final int thickness = 25;
        final int holeStart = segments / 2 + 2;
        final int holeEnd   = holeStart + ( segments / 12 );
        final int theWidth  = ParticleSequencer.width / ( segments / 2 );

        if ( !hasContainer )
        {
            containerX = aX;
            containerY = aY;
        }

        AbstractParticle prev = null;
        AbstractParticle segment;

        for ( int i = 0; i < segments; ++i )
        {
            // this creates a hole for escaping ;-)
//            if ( i >= holeStart && i <= holeEnd )
//            {
//                prev = null;
//                continue;
//            }
            final float radians = ( float ) MathTool.deg2rad(( 360.0f / segments  ) * i );

            final int theX = ( int ) ( containerX + Math.sin( radians ) * aRadius );
            final int theY = ( int ) ( containerY + Math.cos( radians ) * aRadius );

            if ( containerSegments[ i ] != null )
            {
                // updating of existing particles
                containerSegments[ i ].setPosition( FP.fromFloat( theX ), FP.fromFloat( theY ));
            }
            else
            {
                // creating new

                hasContainer = true;

                final boolean isFixed = true;
                //final boolean isFixed = i % 2 == 0;

                // x, y, width, height
                // rotation, fixed
                // mass, elasticity, friction
                segment = new ContainerParticle( theX, theY,
                                                 theWidth, thickness,
                                                 -radians, isFixed, .5f, 5.0f, 0.00f );

                APEngine.addParticle( segment );
                containerSegments[ i ] = segment;

                if ( prev != null )
                    APEngine.addConstraint( new SpringConstraint( prev, segment, 0.5f ));

                prev = segment;
            }
        }
    }

    public void destroyContainer()
    {
        hasContainer = false;

        ArrayList<AbstractConstraint> constraints = APEngine.getAllConstraints();
        int i = constraints.size();

        while ( i-- > 0 )
        {
            APEngine.removeConstraint( constraints.get( i ));
        }

        for ( AbstractParticle p : containerSegments )
        {
            try
            {
                APEngine.removeParticle( p );
            }
            catch ( NullPointerException e ) {}
        }
        containerSegments = new AbstractParticle[ 64 ];
    }

    public final void draw( Canvas c )
    {
        AbstractParticle currentParticle = APEngine.particles;

        while ( currentParticle != null )
        {
            currentParticle.drawParticle( c );
            currentParticle = currentParticle.next;
        }

        if ( DEBUG )
        {
            // show constraints
            ArrayList<AbstractConstraint> constraints = APEngine.getAllConstraints();
            final int size = constraints.size();

            for ( int i = 0; i < size; i++ )
                constraints.get( i ).drawConstraint( c );
        }
    }

    public final void setGravity( int x, int y )
    {
        // gravity -- particles of varying masses are affected the same
        world.setMasslessForce( x, y );
    }
}
