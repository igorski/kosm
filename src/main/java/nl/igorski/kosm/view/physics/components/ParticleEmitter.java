package nl.igorski.kosm.view.physics.components;

import nl.igorski.kosm.model.vo.ParticleEmitterVO;
import nl.igorski.kosm.view.ParticleSequencer;
import blissfulthinking.java.android.ape.APEngine;
import nl.igorski.kosm.definitions.ParticleSounds;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 19-11-12
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public final class ParticleEmitter
{
    private long              _interval;
    private long              _lastEmission;
    private ParticleSequencer _container;
    private float             _xPos;
    private float            _yPos;
    private ParticleSounds   _particleSound;
    private float            _radius;

    public ParticleEmitter( ParticleEmitterVO aVO, ParticleSequencer aSequencer,
                            float aXPos, float aYPos, boolean emitOnStart )
    {
        _interval      = aVO.average();
        _particleSound = aVO.particleSound;
        _radius        = aVO.radius;
        _container     = aSequencer;
        _xPos          = aXPos;
        _yPos          = aYPos;
        _lastEmission  = 0;

        if ( emitOnStart )
            emit( System.currentTimeMillis());
    }

    /* public methods */

    public void emit( final long aCurrentTime )
    {
        final AudioParticle particle = _container.createAudioParticle( _particleSound, _xPos, _yPos,
                                                                       _radius, false );
        particle.emitter = true;
        APEngine.addParticle( particle );

        _lastEmission = aCurrentTime;
    }

    public void update( final long aCurrentTime )
    {
        if (( aCurrentTime - _lastEmission ) >= _interval )
            emit( aCurrentTime );
    }
}
