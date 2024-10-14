cmake_minimum_required(VERSION 3.10)

# Project name
project(mikro_audio_native_lib)

# Add the shared native library
add_library(
        mikroAudioJNI                 # Name of the native library
        SHARED
        ${CMAKE_SOURCE_DIR}/mikro_audio_jni.c
        ${CMAKE_SOURCE_DIR}/../../../../native/src/mikro_audio.c
)

# Include the shared header files from the project root's 'native/src' directory
include_directories(
        ${CMAKE_SOURCE_DIR}/../../../../native/src
)