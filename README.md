Kosm
====

an open source abstract audio application for Android. Kosm uses the accelerometer of your device to
trigger state changes in its audio engine which in turn responds by synthesizing audio on the fly. In
other words : Kosm is a "gravity sequencer", or something.

Keywords : chaos, chance, microtonal.

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

Those of a Unix-bent can run the _build.sh_-file in the root folder of the repository whereas Windows users can run the _build.bat_-file
that resides in the same directory, just make sure "_ndk-build_" and "_swig_" are globally available through the PATH settings of your
system (or adjust the shell scripts accordingly).

After compiling the C++ code, the _nl.igorski.lib.audio.nativeaudio_-namespace should be available to Java.

#### Resolving dependencies

Run the Gradle target "_externals_" to pull all dependent libraries from Github, e.g.:

    gradle externals

#### Building the main application

The usual Gradle suspects such as "_clean_", "_build_", etc. are present. To instantly deploy a debug version of the
application onto an attached Android device / emulator, run :

    gradle installDebug
    
To create a signed release build, add the following into your Gradle's properties file (_~/.gradle/gradle.properties_)
and replace the values accordingly:

    RELEASE_STORE_FILE={path_to_.keystore_file}
    RELEASE_STORE_PASSWORD={password_for_.keystore}
    RELEASE_KEY_ALIAS={alias_for_.keystore}
    RELEASE_KEY_PASSWORD={password_for_.keystore}
    
you can now build and sign a releasable APK by running:

    gradle build
    
after which the built and signed APK is available in _./build/outputs/apk-kosm-release.apk_
    
#### Application outline

The main Activity _Kosm.java_ spawns an instance of *ParticleSequencer*. This is the main class
responsible for the applications behaviour.

Inside the ParticleSequencer there are two threads running :

 * **ViewRenderer** responsible for drawing the "world" and updating the physics engine (physicsWorld)
 * **MWEngine** responsible for rendering synthesized audio using the native layer engine

The ParticleSequencer listens to touch and sensor change events and delegates actions accordingly (see its "event handlers" section). This is basically what the app boils down to: monitoring sensor changes and synthesizing colliding "particles"; upon touching the screen surface, the ViewRenderer will spawn particles. Depending on the sequencer mode these particles have different behaviours (e.g. are static, respond to gravity, emit clones at a steady rate, etc.), when these particles collide with one another they will synthesize their audio. The "mass" and type of a particle determines the pitch and waveform used for synthesis (see **AudioParticle**).

Most of the application logic is command-based (using a simplified abstraction of the PureMVC framework, see all _Core.notify()_ invocations)
and the code for these commands should be self explanatory; a command basically ties together all actors of the application to execute a
state change. E.g. there are commands for creating the effects chain, opening/closing of the submenus, toggling sequencer modes, etc.
As such most of the logic is small and self contained.
