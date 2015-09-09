/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package nl.igorski.lib.audio.nativeaudio;

public class JNISampleManager {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected JNISampleManager(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(JNISampleManager obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        NativeAudioEngineJNI.delete_JNISampleManager(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public static SWIGTYPE_p_AudioBuffer getSample(String aIdentifier) {
    long cPtr = NativeAudioEngineJNI.JNISampleManager_getSample(aIdentifier);
    return (cPtr == 0) ? null : new SWIGTYPE_p_AudioBuffer(cPtr, false);
  }

  public static boolean hasSample(String aIdentifier) {
    return NativeAudioEngineJNI.JNISampleManager_hasSample(aIdentifier);
  }

  public static void setSample(String aKey, int aBufferLength, int aChannelAmount, double[] aBuffer, double[] aOptRightBuffer) {
    NativeAudioEngineJNI.JNISampleManager_setSample(aKey, aBufferLength, aChannelAmount, aBuffer, aOptRightBuffer);
  }

  public static void cacheTable(int tableLength, int waveformType) {
    NativeAudioEngineJNI.JNISampleManager_cacheTable__SWIG_0(tableLength, waveformType);
  }

  public static void cacheTable(int tableLength, int waveformType, double[] aBuffer) {
    NativeAudioEngineJNI.JNISampleManager_cacheTable__SWIG_1(tableLength, waveformType, aBuffer);
  }

  public JNISampleManager() {
    this(NativeAudioEngineJNI.new_JNISampleManager(), true);
  }

}