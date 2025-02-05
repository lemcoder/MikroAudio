#ifndef MIKRO_AUDIO_H
#define MIKRO_AUDIO_H

#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include "miniaudio/miniaudio.h"

// Playback

typedef void (*ma_data_callback)(ma_device* pDevice, void* pOutput, const void* pInput, ma_uint32 frameCount);

ma_device* initialize_playback_device(int channelCount, int sampleRate, ma_data_callback dataCallback, ma_format format, void* userData);

void uninitialize_playback_device(ma_device* device);

// Recording

int initialize_recording(long long int sizeInBytes, int channelCount, int sampleRate);

void uninitialize_recording();

void* stop_recording(long long int sizeInBytes);

int start_recording();

#endif // MIKRO_AUDIO_H