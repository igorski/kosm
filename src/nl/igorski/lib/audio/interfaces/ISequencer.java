package nl.igorski.lib.audio.interfaces;

import nl.igorski.lib.audio.MWEngine;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 6/8/12
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISequencer
{
    /**
     * an ISequencer is the interface feeding the AudioRenderer the
     * events and properties used for audio output
     */

    /**
     * Sequencers can have different properties, return the
     * amount of time intervals within a bar (for instance 16
     * for creation of 16th notes, 32, 64, 128 )
     *
     * @return {int}
     */
    public int getStepsPerBar();

    /**
     * how many measures / bars are
     * mapped in the sequencer ?
     * @return {int}
     */
    public int getMeasures();

    /**
     * A sequencer's tempo is linked to a AudioRenderer as
     * the renderer must invoke the tempo update outside of
     * its read / render cycles
     *
     * Typical behaviour for handling an update in tempo is
     * altering view states / updating AudioEvents
     */
    public void handleTempoUpdate();

    /**
     * Every tick (each step in a bar) that has passed, invokes
     * this method in the sequencer. Likely for updating views
     *
     * @param aPosition {int}
     */
    public void handleSequencerPositionUpdate( int aPosition );

    /**
     * There is a set interval for recording live output
     * where the recorded buffer is passed for writing to disk, this
     * should overcome unnecessarily large memory allocation while recording
     *
     * @param aIsComplete {boolean} whether this is the last update and recording has completed
     * @param aRecordingFileNum {int} the index of the last written recording
     */
    public void handleRecordingUpdate( boolean aIsComplete, int aRecordingFileNum );

    public MWEngine getAudioEngine();

    public boolean usePrecaching();

    /**
     * invoked when all hell breaks loose...
     * this implies the engine could not be started
     */
    public abstract void handleEnginePanic();
}
