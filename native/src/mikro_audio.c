#include "mikro_audio.h"

#ifndef MIKRO_AUDIO_C
#define MIKRO_AUDIO_C

// Miniaudio Configuration
#define MINIAUDIO_IMPLEMENTATION
#include "miniaudio/miniaudio.h"

// #define MA_NO_RUNTIME_LINKING

static ma_device *pPlaybackDevice = NULL;
static void *pPlaybackBuffer = NULL;
static ma_uint64 playbackBufferSize = 0;
static ma_uint64 playbackCursor = 0;
static ma_uint32 playbackBytesPerFrame = 0;

static void playback_data_callback(ma_device *pDevice, void *pOutput, const void *pInput, ma_uint32 frameCount) {
    if (!pPlaybackBuffer || playbackBufferSize == 0 || playbackBytesPerFrame == 0) {
        return;
    }

    ma_uint64 byteCount = frameCount * playbackBytesPerFrame;
    ma_uint32 bytesToPlay;

    if (playbackCursor >= playbackBufferSize) {
        playbackCursor = 0;  // Loop playback by resetting the cursor
    }

    if (playbackCursor + byteCount > playbackBufferSize) {
        bytesToPlay = playbackBufferSize - playbackCursor;
        frameCount = bytesToPlay / playbackBytesPerFrame;
    } else {
        bytesToPlay = byteCount;
    }

    if (bytesToPlay > 0) {
        ma_copy_pcm_frames(pOutput, pPlaybackBuffer + playbackCursor, frameCount, pDevice->playback.format, pDevice->playback.channels);
        playbackCursor += bytesToPlay;
    }

    (void)pInput;  // Unused parameter
}

int initialize_playback_device(int channelCount, int sampleRate) {
    ma_device_config deviceConfig;
    ma_result result;

    pPlaybackDevice = (ma_device *)malloc(sizeof(ma_device));
    if (!pPlaybackDevice) {
        return MA_OUT_OF_MEMORY;
    }

    deviceConfig = ma_device_config_init(ma_device_type_playback);
    deviceConfig.playback.format = ma_format_f32;
    deviceConfig.playback.channels = channelCount;
    deviceConfig.sampleRate = sampleRate;
    deviceConfig.dataCallback = playback_data_callback;
    deviceConfig.noFixedSizedCallback = MA_TRUE;

    result = ma_device_init(NULL, &deviceConfig, pPlaybackDevice);
    if (result != MA_SUCCESS) {
        free(pPlaybackDevice);
        pPlaybackDevice = NULL;
        return MA_ERROR;
    }

    playbackBytesPerFrame = ma_get_bytes_per_frame(deviceConfig.playback.format, deviceConfig.playback.channels);
    return MA_SUCCESS;
}

int set_playback_buffer(void *buffer, long long int sizeInBytes) {
    if (!pPlaybackDevice) {
        return MA_ERROR;
    }

    if (pPlaybackBuffer) {
        free(pPlaybackBuffer);
    }

    if (!buffer) {
        return MA_ERROR;
    }

    pPlaybackBuffer = calloc(1, sizeInBytes);
    if (!pPlaybackBuffer) {
        return MA_ERROR;
    }

    memcpy(pPlaybackBuffer, buffer, sizeInBytes);
    playbackBufferSize = sizeInBytes;

    return MA_SUCCESS;
}

void uninitialize_playback_device() {
    if (pPlaybackDevice) {
        ma_device_uninit(pPlaybackDevice);
        free(pPlaybackDevice);
        pPlaybackDevice = NULL;
    }

    if (pPlaybackBuffer) {
        free(pPlaybackBuffer);
        pPlaybackBuffer = NULL;
    }

}

int start_playback() {
    if (!pPlaybackDevice) {
        return MA_ERROR;
    }

    ma_result result = ma_device_start(pPlaybackDevice);
    if (result != MA_SUCCESS) {
        return MA_ERROR;
    }

    return MA_SUCCESS;
}

void stop_playback() {
    if (!pPlaybackDevice) {
        return;
    }

    ma_device_stop(pPlaybackDevice);
}

static ma_device recordingDevice;
static void *pCaptureBuffer = NULL;
static ma_uint32 recordedBytes = 0;
static ma_uint32 captureRequiredSizeBytes = 0;
static ma_uint32 captureBytesPerFrame = 0;

static void capture_data_callback(ma_device *pDevice, void *pOutput, const void *pInput, ma_uint32 frameCount) {
    if (!pCaptureBuffer || captureRequiredSizeBytes == 0 || captureBytesPerFrame == 0) {
        return;
    }

    ma_uint32 byteCount = captureBytesPerFrame * frameCount;
    ma_uint32 bytesToSave;

    if (recordedBytes >= captureRequiredSizeBytes) {
        return;
    }

    if (recordedBytes + byteCount > captureRequiredSizeBytes) {
        bytesToSave = captureRequiredSizeBytes - recordedBytes;
        frameCount = bytesToSave / captureBytesPerFrame;
    } else {
        bytesToSave = byteCount;
    }

    ma_copy_pcm_frames((ma_uint8 *)pCaptureBuffer + recordedBytes, pInput, frameCount, pDevice->capture.format, pDevice->capture.channels);
    recordedBytes += bytesToSave;

    (void)pOutput;
}

int initialize_recording(long long int sizeInBytes, int channelCount, int sampleRate) {
    ma_device_config deviceConfig;
    ma_result result;

    deviceConfig = ma_device_config_init(ma_device_type_capture);
    deviceConfig.capture.format = ma_format_f32;
    deviceConfig.capture.channels = channelCount;
    deviceConfig.sampleRate = sampleRate;
    deviceConfig.dataCallback = capture_data_callback;
    deviceConfig.noFixedSizedCallback = MA_TRUE;

    result = ma_device_init(NULL, &deviceConfig, &recordingDevice);
    if (result != MA_SUCCESS) {
        return MA_ERROR;
    }

    captureRequiredSizeBytes = sizeInBytes;
    captureBytesPerFrame = ma_get_bytes_per_frame(ma_format_f32, channelCount);

    pCaptureBuffer = calloc(1, sizeInBytes);
    if (!pCaptureBuffer) {
        return MA_ERROR;
    }

    return MA_SUCCESS;
}

void uninitialize_recording() {
    ma_device_uninit(&recordingDevice);
    recordedBytes = 0;
    captureRequiredSizeBytes = 0;

    if (pCaptureBuffer) {
        free(pCaptureBuffer);
        pCaptureBuffer = NULL;
    }

}

void *stop_recording(long long int sizeInBytes) {
    ma_device_stop(&recordingDevice);

    void *tmpBuffer = malloc(sizeInBytes);
    memcpy(tmpBuffer, pCaptureBuffer, sizeInBytes);

    return tmpBuffer;
}

int start_recording() {
    recordedBytes = 0;

    if (!pCaptureBuffer) {
        return MA_ERROR;
    }

    if (ma_device_start(&recordingDevice) != MA_SUCCESS) {
        ma_device_uninit(&recordingDevice);
        return MA_ERROR;
    }

    return MA_SUCCESS;
}

#endif // MIKRO_AUDIO_C
