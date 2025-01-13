#include <jni.h>
#include "mikro_audio.h"

JNIEXPORT jint JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidPlaybackManager_initializePlaybackNative(JNIEnv *env, jobject thiz, jint channel_count, jint sample_rate) {
    // return initialize_playback_device(channel_count, sample_rate); FIXME
    return 0;
}

JNIEXPORT jint JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidPlaybackManager_setPlaybackBufferNative(JNIEnv *env, jobject thiz, jbyteArray buffer, jint size) {
    jbyte *nativeBuffer = (*env)->GetByteArrayElements(env, buffer, NULL);
    int result = set_playback_buffer(nativeBuffer, size);
    (*env)->ReleaseByteArrayElements(env, buffer, nativeBuffer, 0);
    return result;
}

JNIEXPORT jint JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidPlaybackManager_startPlaybackNative(JNIEnv *env, jobject thiz) {
    return start_playback();
}

JNIEXPORT void JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidPlaybackManager_stopPlaybackNative(JNIEnv *env, jobject thiz) {
    stop_playback();
}

JNIEXPORT void JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidPlaybackManager_uninitializePlaybackNative(JNIEnv *env, jobject thiz) {
    uninitialize_playback_device();
}


JNIEXPORT jint JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidRecordManager_initializeRecordingNative(JNIEnv *env, jobject thiz, jint channel_count, jint sample_rate, jlong size_in_bytes) {
    return initialize_recording(size_in_bytes, channel_count, sample_rate);
}

JNIEXPORT jint JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidRecordManager_startRecordingNative(JNIEnv *env, jobject thiz) {
    return start_recording();
}

JNIEXPORT jbyteArray JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidRecordManager_stopRecordingNative(JNIEnv *env, jobject thiz, jlong size_in_bytes) {
    void *recordedData = stop_recording(size_in_bytes);

    jbyteArray result = (*env)->NewByteArray(env, (jint) size_in_bytes);
    if (result == NULL) {
        return NULL;
    }

    (*env)->SetByteArrayRegion(env, result, 0, (jint) size_in_bytes, (jbyte *) recordedData);
    free(recordedData);
    return result;
}

JNIEXPORT void JNICALL
Java_pl_lemanski_mikroaudio_internal_AndroidRecordManager_uninitializeRecordingNative(JNIEnv *env, jobject thiz) {
    uninitialize_recording();
}