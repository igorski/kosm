Kosm
====

an open source abstract audio application for Android. Kosm uses the accelerometer of your device to
trigger state changes in its audio engine which in turn responds by synthesizing audio on the fly. In
other words : Kosm is a "gravity sequencer", or something.

### MWEngine audio engine

Kosm is built on top of MWEngine, an open source audio engine for Android by igorski. MWEngine is
written in C++, using OpenSL for low latency performance. The engine has been written for the MikroWave
synthesis/sequencing-application and works from API level 9 (Android 2.3/Gingerbread) and up. Though the
engine is written in C++  (and can be used solely within this context), the library is built using JNI
(Java Native Interface) allowing its methods to be exposed to Java while still executing in a native layer
outside of the Dalvik/ART VM. In other words : high performance of the engine is ensured by the native layer
operations, while ease of development is ensured by keeping application logic / UI within the realm of the
Android Java SDK. For more details and plenty of Wiki documentation you can go to the MWEngine repository :

https://github.com/igorski/MWEngine

Kosm inherits most of the basic MWEngine classes, see the _/jni/kosm_-folder for the rendering of
Kosm-specific "AudioParticleEvents".

### APE Physics engine

Kosm relies on the APE physics engine and uses a modified version of the Android version. Sadly, the
original Android library doesn't seem to be maintained anymore (the old Subversion repository on Google Code
has been migrated to Github though : https://github.com/msoftware/ape-physics-for-android)

Credits for APE go to Alec Cove, Theo Galanakis (Java port) and Michiel van den Anker (Android port).

### Build instructions

You will need both the Android SDK and the Android NDK installed. The Android NDK is used to compile the
audio engine. All build commands bar the compilation of the audio engine code is done via Gradle.

#### Compiling the audio engine

The makefile (_/jni/Android.mk_) will compile the MWEngine audio engine with all available modules.

Those of a Unix-bent can run the _build.sh_-file in the root folder of the repository whereas Windows users can run the
_build.bat_-file that resides in the same directory, just make sure "_ndk-build_" and "_swig_" are globally available
through the PATH settings of your system (or adjust the shell scripts accordingly).

#### Resolving dependencies

Run the Gradle target "externals" to pull all dependent libraries from Github.

#### Building the main application

The usual Gradle suspects such as "clean", "build", etc. work. To instantly deploy a debug version of the
application onto an attached Android device / emulator, run :

    gradle installDebug
    
#### Application outline

The main Activity _Kosm.java_ spawns an instance of *ParticleSequencer*. This is the main class
responsible for the applications behaviour.

Inside the ParticleSequencer there are two threads running :

 * *ViewRenderer* responsible for drawing the "world" and updating the physics engine (physicsWorld)
 * *MWEngine* responsible for rendering synthesized audio using the native layer engine
 
Additionally, the ParticleSequencer listens to touch and sensor change events and delegates actions
accordingly (see "event handlers" section).

Most of the application logic is command based (see _Core.notify()_ invocations) in a simplified abstraction
of the PureMVC framework, but the code for these should be self explanatory enough.
