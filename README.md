Kosm
====

an open source abstract audio application for Android. Kosm uses the accelerometer of your device to
trigger state changes in its audio engine, in other words : Kosm is a "gravity sequencer", or something.

### MWEngine

Kosm is built on top of MWEngine, the open source audio engine developed for MikroWave. MWEngine is
an audio engine for Android written in C++, using OpenSL for low latency performance. The engine has
been written for the MikroWave synthesis/sequencing-application and works from API level 9 (Android 2.3/Gingerbread)
and up. Though the engine is written in C++  (and can be used solely within this context), the library is built using JNI
(Java Native Interface) allowing its methods to be exposed to Java while still executing in a native layer outside of
the Dalvik/ART VM. In other words : high performance of the engine is ensured by the native layer operations, while
ease of development is ensured by keeping application logic / UI within the realm of the Android Java SDK.
For more details and plenty of Wiki documentation you can go to the MWEngine repository :

https://github.com/igorski/MWEngine

Kosm uses a custom extension of the basic MWEngine classes (although a cleanup is pending!). See the
_/jni/kosm_-folder for the rendering of AudioParticleEvents.

### Build instructions

You will need both the Android SDK and the Android NDK installed. The Android NDK is used to compile the
audio engine.

#### Compiling the audio engine

The makefile (_/jni/Android.mk_) will compile the MWEngine audio engine with all available modules.

Those of a Unix-bent can run the _build.sh_-file in the root folder of the repository whereas Windows users can run the
_build.bat_-file that resides in the same directory, just make sure "_ndk-build_" and "_swig_" are globally available
through the PATH settings of your system (or adjust the shell scripts accordingly).

#### Building the main application

Run the ant script with the "install" target.
