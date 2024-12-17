# Kosm

an open source abstract audio application for Android. Kosm uses the accelerometer of your device to
trigger state changes in its audio engine which in turn responds by synthesizing audio on the fly. In
other words : Kosm is a "gravity sequencer", or something.

Keywords : chaos, chance, microtonal.

## A word of codebase caution

Kosm is an old, old lady. The codebase has been updated to work with modern Gradle based toolchains
and to be available to work on Google Plays latest platform requirements (starting from API 34), but the code itself goes back
quite a few years. Heck, it was originally built to run on _Android 2.3_.

Kosm had been in maintenance mode for a few years and was occassionally updated whenever Google Play's policies indicated that
a change was necessary to keep the app available. As of December 2024, this repository is **archived**.

At the moment of archival, the app should compile and run, but note this repository is no **longer maintained**. Basically, you're on your own dealing
with any future (breaking) changes to Android API's. Regardless, it shouldn't prove _too_ troublesome. *knocks wood* 😬

### MWEngine audio engine

Kosm was built on top of MWEngine, an open source audio engine for Android from the same developer. MWEngine is
written in C++, leveraging OpenSL for low latency performance. Though the engine is written in C++ (and can be
used solely within this context), the library is wrapped using JNI (Java Native Interface) allowing its methods
to be exposed to Java/Kotlin while still executing in a native layer outside of the Dalvik/ART VM.

In other words : high performance of the engine is ensured by the native layer operations, while ease of
development is ensured by keeping application logic / UI within the realm of the Android Java SDK. For more
details and plenty of up-to-date Wiki documentation you can go to the MWEngine repository :

[MWEngine on Github](https://github.com/igorski/MWEngine)

Note: the supplied JNI code in this repository is a **fork of an older MWEngine version**. If you wish to use
MWEngine with the latest performance improvements, processors and AAudio support, visit the aforementioned
project URL. It is recommended to build MWEngine as an .AAR library instead of copying files and makelists.

Kosm inherits most of the basic MWEngine classes, with the `/jni/kosm`-folder containing the Kosm-specific `AudioParticleEvent` class.

### APE Physics engine

Kosm relies on the APE physics engine and uses a modified version of the Android version. Sadly, the
original Android library doesn't seem to be maintained anymore (the old Subversion repository on Google Code
has been [migrated to Github](https://github.com/msoftware/ape-physics-for-android) though)

Credits for APE go to Alec Cove, Theo Galanakis (Java port) and Michiel van den Anker (Android port).

### Build instructions

You will need both the Android SDK and the Android NDK installed. The Android NDK is used to compile the
audio engine. All build commands bar the compilation of the audio engine code is done via Gradle.

#### Compiling the audio engine

The makefile `CMakeLists.txt` will compile the MWEngine audio engine with all available modules.
After compiling the C++ code, the `nl.igorski.mwengine.core`-namespace should be available to Java.

#### Building the main application

The usual Gradle suspects such as `clean`, `build`, etc. are present. To instantly deploy a debug version of the
application onto an attached Android device / emulator, run :

```
gradle installDebug
```

To create a signed release build, add the following into your Gradle's properties file (_~/.gradle/gradle.properties_)
and replace the values accordingly:

```
RELEASE_STORE_FILE={path_to_.keystore_file}
RELEASE_STORE_PASSWORD={password_for_.keystore}
RELEASE_KEY_ALIAS={alias_for_.keystore}
RELEASE_KEY_PASSWORD={password_for_.keystore}
```

you can now build and sign a releasable APK by running:

```
gradle build
```

after which the built and signed APK is available in `./build/outputs/apk-kosm-release.apk`
    
#### Application outline

The main Activity `Kosm.java` spawns an instance of *ParticleSequencer*. This is the main class
responsible for the applications behaviour.

Inside the ParticleSequencer there are two threads running :

 * **ViewRenderer** responsible for drawing the "world" and updating the physics engine (physicsWorld)
 * **MWEngine** responsible for rendering synthesized audio using the native layer engine

The ParticleSequencer listens to touch and sensor change events and delegates actions accordingly (see its "event handlers" section). This is basically what the app boils down to: monitoring sensor changes and synthesizing colliding "particles"; upon touching the screen surface, the ViewRenderer will spawn particles. Depending on the sequencer mode these particles have different behaviours (e.g. are static, respond to gravity, emit clones at a steady rate, etc.), when these particles collide with one another they will synthesize their audio. The "mass" and type of a particle determines the pitch and waveform used for synthesis (see **AudioParticle**).

Most of the application logic is command-based (using a simplified abstraction of the PureMVC framework, see all _Core.notify()_ invocations)
and the code for these commands should be self explanatory; a command basically ties together all actors of the application to execute a
state change. E.g. there are commands for creating the effects chain, opening/closing of the submenus, toggling sequencer modes, etc.
As such most of the logic is small and self contained.
