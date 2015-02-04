package nl.igorski.kosm.model;

import nl.igorski.kosm.controller.startup.PrepareInstrumentsCommand;
import nl.igorski.kosm.definitions.ParticleSounds;
import nl.igorski.lib.audio.interfaces.IUpdateableInstrument;
import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;
import nl.igorski.lib.framework.Core;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 25-01-15
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class KosmInstruments
{
    private static KosmInstruments          INSTANCE;
    private Vector<InternalSynthInstrument> _instruments;

    public KosmInstruments( IUpdateableInstrument aListener )
    {
        INSTANCE = this;

        // create the instruments

        _instruments = new Vector<InternalSynthInstrument>( 3 );

        for ( int i = 0; i < 3; ++i ) {
            _instruments.add( i, new InternalSynthInstrument( i, aListener ));
        }

        Core.notify( new PrepareInstrumentsCommand(), _instruments );
    }

    /* public methods */

    public static InternalSynthInstrument getInstrumentByParticleSound( ParticleSounds aSound )
    {
        switch ( aSound )
        {
            default:
            case PARTICLE_SINE:
            case PARTICLE_SAW:
                return INSTANCE._instruments.get( 0 );

            case PARTICLE_KICK:
            case PARTICLE_TWANG:
                return INSTANCE._instruments.get( 1 );

            case PARTICLE_PAD:
                return INSTANCE._instruments.get( 2 );
        }
    }
}
