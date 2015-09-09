package nl.igorski.kosm.view.physics.components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import blissfulthinking.java.android.ape.RectangleParticle;
import blissfulthinking.java.android.apeforandroid.FP;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 14-08-12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public final class ContainerParticle extends RectangleParticle
{
    private Paint _paint;

    private Path _path;

    /**
     * @param x The initial x position.
     * @param y The initial y position.
     * @param width The width of this particle.
     * @param height The height of this particle.
     * @param rotation The rotation of this particle in radians.
     * @param fixed Determines if the particle is fixed or not. Fixed particles
     * are not affected by forces or collisions and are good to use as surfaces.
     * Non-fixed particles move freely in response to collision and forces.
     * @param mass The mass of the particle
     * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
     * @param friction The surface friction of the particle.
     */
    public ContainerParticle( float x, float y, float width, float height, float rotation, boolean fixed,
                              float mass, float elasticity, float friction )
    {
        super( x, y, width, height, rotation, fixed, mass, elasticity, friction, true );

        _paint = new Paint();
        _paint.setColor( Color.BLACK );
        _paint.setStyle( Paint.Style.FILL );

        _path = new Path();
    }

    /* public */

    @Override
    public final void resolveCollision( int[] mtd, int[] vel, int[] n, int d, int o )
    {
        super.resolveCollision( mtd, vel, n, d, o );
        //setFixed( false );
    }

    @Override
    public final void drawParticle( Canvas c )
    {
        _path.reset();

        _path.moveTo( FP.toFloat( cornerPositions[ 0 ].x ), FP.toFloat( cornerPositions[ 0 ].y ));
        _path.lineTo( FP.toFloat( cornerPositions[ 1 ].x ), FP.toFloat( cornerPositions[ 1 ].y ));
        _path.lineTo( FP.toFloat( cornerPositions[ 2 ].x ), FP.toFloat( cornerPositions[ 2 ].y ));
        _path.lineTo( FP.toFloat( cornerPositions[ 3 ].x ), FP.toFloat( cornerPositions[ 3 ].y ));
        _path.lineTo( FP.toFloat( cornerPositions[ 0 ].x ), FP.toFloat( cornerPositions[ 0 ].y ));
        _path.close();

        c.drawPath( _path, _paint );
     }
}
