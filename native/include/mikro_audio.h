#ifndef MIKRO_AUDIO_H
#define MIKRO_AUDIO_H

#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include "miniaudio/miniaudio.h"

// Playback

struct ma_playback
{
    const ma_device_id* pDeviceID;
    ma_format format;
    ma_uint32 channels;
    ma_channel* pChannelMap;
    ma_channel_mix_mode channelMixMode;
    ma_bool32 calculateLFEFromSpatialChannels;
    ma_share_mode shareMode;
};

struct ma_capture
{
    const ma_device_id* pDeviceID;
    ma_format format;
    ma_uint32 channels;
    ma_channel* pChannelMap;
    ma_channel_mix_mode channelMixMode;
    ma_bool32 calculateLFEFromSpatialChannels;
    ma_share_mode shareMode;
};

struct ma_wasapi
{
    ma_wasapi_usage usage;
    ma_bool8 noAutoConvertSRC;
    ma_bool8 noDefaultQualitySRC;
    ma_bool8 noAutoStreamRouting;
    ma_bool8 noHardwareOffloading;
    ma_uint32 loopbackProcessID;
    ma_bool8 loopbackProcessExclude;
};

struct ma_alsa
{
    ma_bool32 noMMap;
    ma_bool32 noAutoFormat;
    ma_bool32 noAutoChannels;
    ma_bool32 noAutoResample;
};

struct ma_pulse
{
    const char* pStreamNamePlayback;
    const char* pStreamNameCapture;
};

struct ma_coreaudio
{
    ma_bool32 allowNominalSampleRateChange;
};

struct ma_opensl
{
    ma_opensl_stream_type streamType;
    ma_opensl_recording_preset recordingPreset;
    ma_bool32 enableCompatibilityWorkarounds;
};

struct ma_aaudio
{
    ma_aaudio_usage usage;
    ma_aaudio_content_type contentType;
    ma_aaudio_input_preset inputPreset;
    ma_aaudio_allowed_capture_policy allowedCapturePolicy;
    ma_bool32 noAutoStartAfterReroute;
    ma_bool32 enableCompatibilityWorkarounds;
};


struct ma_device_config_flat
{
    ma_device_type deviceType;
    ma_uint32 sampleRate;
    ma_uint32 periodSizeInFrames;
    ma_uint32 periodSizeInMilliseconds;
    ma_uint32 periods;
    ma_performance_profile performanceProfile;
    ma_bool8 noPreSilencedOutputBuffer;
    ma_bool8 noClip;
    ma_bool8 noDisableDenormals;
    ma_bool8 noFixedSizedCallback;
    ma_device_data_proc dataCallback;
    ma_device_notification_proc notificationCallback;
    ma_stop_proc stopCallback;
    void* pUserData;
    ma_resampler_config resampling;
    struct ma_playback playback;
    struct ma_capture capture;
    struct ma_wasapi wasapi;
    struct ma_alsa alsa;
    struct ma_pulse pulse;
    struct ma_coreaudio coreaudio;
    struct ma_opensl opensl;
    struct ma_aaudio aaudio;
};


typedef void (*ma_data_callback)(ma_device* pDevice, void* pOutput, const void* pInput, ma_uint32 frameCount);

// Recording

int initialize_recording(long long int sizeInBytes, int channelCount, int sampleRate);

void uninitialize_recording();

void* stop_recording(long long int sizeInBytes);

int start_recording();

#endif // MIKRO_AUDIO_H