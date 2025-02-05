#include "mikro_audio.h"
#include <string.h>
#include <stdlib.h>

#ifndef MIKRO_AUDIO_C
#define MIKRO_AUDIO_C

// Miniaudio Configuration
#define MINIAUDIO_IMPLEMENTATION
// #define MA_NO_RUNTIME_LINKING

#include "miniaudio/miniaudio.h"

ma_device* initialize_playback_device(int channelCount, int sampleRate, ma_data_callback dataCallback, ma_format format, void* userData) {
    ma_device* device = (ma_device*)malloc(sizeof(ma_device));
    ma_device_config deviceConfig;
    ma_result result;

    deviceConfig = ma_device_config_init(ma_device_type_playback);
    deviceConfig.playback.format = format;
    deviceConfig.playback.channels = channelCount;
    deviceConfig.sampleRate = sampleRate;
    deviceConfig.dataCallback = dataCallback;
    deviceConfig.noFixedSizedCallback = MA_TRUE;
    deviceConfig.pUserData = userData;

    result = ma_device_init(NULL, &deviceConfig, device);
    if (result != MA_SUCCESS) {
         return NULL;
    }

    return device;
}

void uninitialize_playback_device(ma_device* device) {
    if (device) {
        ma_device_uninit(device);
        free(device);
    }
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
