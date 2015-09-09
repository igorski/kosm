package nl.igorski.lib.audio.managers;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 07-01-13
 * Time: 00:15
 * To change this template use File | Settings | File Templates.
 */
public final class InternalSampleManager
{
    private HashMap<String, double[]> _sampleMap;

    public boolean transferredToNativeEngine = false;   // whether the contents of this manager
                                                        // have been transferred to the Native engine

    /**
     * InternalSampleManager can hold samples which have been
     * loaded off the filesystem. For convenience, these samples should
     * be available to the native engine (which has its own SampleManager)
     * this class is merely a bridge between the filesystem and the engine.
     *
     * To save memory, you can flush these samples upon transfer to the
     * native engine
     */
    public InternalSampleManager()
    {
        _sampleMap = new HashMap<String, double[]>();
    }

    /* public methods */

    public void setSample( String aIdentifier, double[] aBuffer )
    {
        _sampleMap.put( aIdentifier, aBuffer );
    }

    public double[] getSample( String aIdentifier )
    {
        return _sampleMap.get( aIdentifier );
    }

    public boolean hasSample( String aIdentifier )
    {
        return _sampleMap.containsKey( aIdentifier );
    }

    public void flushSamples()
    {
        _sampleMap.clear();
    }
}
