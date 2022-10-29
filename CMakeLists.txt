# The MIT License (MIT)
#
# Copyright (c) 2013-2022 Igor Zinken - https://www.igorski.nl
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#
cmake_minimum_required(VERSION 3.4.1)

# policies for SWIG
cmake_policy(SET CMP0086 NEW)
cmake_policy(SET CMP0078 NEW)

# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

Project(MWEngine)
set(target mwengine)

# architecture-specific compiler flags

if (${ANDROID_ABI} MATCHES "x86_64")
    SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel")
endif()

# source folders

set(CPP_SRC  ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp)
set(JAVA_SRC ${CMAKE_CURRENT_SOURCE_DIR}/src/main/java)

# Include location of the header files
include_directories(${CPP_SRC}/)

# core engine

set(MWENGINE_CORE_SOURCES ${CPP_SRC}/global.cpp
                          ${CPP_SRC}/audioengine.cpp
                          ${CPP_SRC}/audiobuffer.cpp
                          ${CPP_SRC}/audiochannel.cpp
                          ${CPP_SRC}/processingchain.cpp
                          ${CPP_SRC}/ringbuffer.cpp
                          ${CPP_SRC}/sequencer.cpp
                          ${CPP_SRC}/sequencercontroller.cpp
                          ${CPP_SRC}/wavetable.cpp
                          ${CPP_SRC}/drivers/opensl_io.c
                          ${CPP_SRC}/events/baseaudioevent.cpp
                          ${CPP_SRC}/events/basecacheableaudioevent.cpp
                          ${CPP_SRC}/events/sampleevent.cpp
                          ${CPP_SRC}/generators/wavegenerator.cpp
                          ${CPP_SRC}/instruments/baseinstrument.cpp
                          ${CPP_SRC}/instruments/sampledinstrument.cpp
                          ${CPP_SRC}/messaging/notifier.cpp
                          ${CPP_SRC}/messaging/observer.cpp
                          ${CPP_SRC}/modules/envelopefollower.cpp
                          ${CPP_SRC}/modules/lfo.cpp
                          ${CPP_SRC}/modules/routeableoscillator.cpp
                          ${CPP_SRC}/processors/basedynamicsprocessor.cpp
                          ${CPP_SRC}/processors/baseprocessor.cpp
                          ${CPP_SRC}/utilities/bufferutility.cpp
                          ${CPP_SRC}/utilities/bulkcacher.cpp
                          ${CPP_SRC}/utilities/diskwriter.cpp
                          ${CPP_SRC}/utilities/samplemanager.cpp
                          ${CPP_SRC}/utilities/tablepool.cpp
                          ${CPP_SRC}/utilities/wavewriter.cpp
                          ${CPP_SRC}/utilities/utils.cpp)

# synthesis (can be omitted if your use case only concerns sample based playback)

set(MWENGINE_SYNTH_SOURCES ${CPP_SRC}/events/basesynthevent.cpp
                           ${CPP_SRC}/events/synthevent.cpp
                           ${CPP_SRC}/modules/adsr.cpp
                           ${CPP_SRC}/modules/arpeggiator.cpp
                           ${CPP_SRC}/instruments/synthinstrument.cpp)

# effects processors

set(MWENGINE_PROCESSORS ${CPP_SRC}/processors/bitcrusher.cpp
                        ${CPP_SRC}/processors/compressor.cpp
                        ${CPP_SRC}/processors/decimator.cpp
                        ${CPP_SRC}/processors/delay.cpp
                        ${CPP_SRC}/processors/filter.cpp
                        ${CPP_SRC}/processors/finalizer.cpp
                        ${CPP_SRC}/processors/formantfilter.cpp
                        ${CPP_SRC}/processors/limiter.cpp
                        ${CPP_SRC}/processors/lpfhpfilter.cpp
                        ${CPP_SRC}/processors/pitchshifter.cpp
                        ${CPP_SRC}/processors/waveshaper.cpp)

# Kosm specific MWEngine additions

set(KOSM_SOURCES ${CPP_SRC}/kosm/audioparticleevent.cpp)

# when using the library solely from C++ you can omit bundling JNI_SOURCES (or SWIG wrapping)

set(JNI_SOURCES ${CPP_SRC}/jni/javabridge.cpp
                ${CPP_SRC}/jni/javautilities.cpp)

add_library(${target} STATIC
                     ${MWENGINE_CORE_SOURCES}
                     ${MWENGINE_SYNTH_SOURCES}
                     ${MWENGINE_PROCESSORS}
                     ${KOSM_SOURCES}
                     ${JNI_SOURCES})

# link all the NDK libraries used by the project

target_link_libraries(${target} android atomic log OpenSLES)

#####################################
## SWIG JNI to Java class wrapping ##
#####################################

find_package(SWIG REQUIRED)
include(${SWIG_USE_FILE})

set_property(SOURCE ${CPP_SRC}/mwengine.i PROPERTY CPLUSPLUS ON)

set(CMAKE_SWIG_FLAGS -DNO_RTIO -c++ -lcarrays.i -package nl.igorski.mwengine.core)
set(CMAKE_SWIG_OUTDIR ${JAVA_SRC}/nl/igorski/mwengine/core)

# Export a wrapper file to Java, and link with the created C++ library

swig_add_library(${target}_wrapped LANGUAGE java SOURCES ${CPP_SRC}/mwengine.i)
swig_link_libraries(${target}_wrapped ${target})
