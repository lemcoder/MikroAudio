#ifndef MIKRO_AUDIO_H
#define MIKRO_AUDIO_H

#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include "miniaudio/miniaudio.h"

typedef void (*ma_data_callback)(ma_device* pDevice, void* pOutput, const void* pInput, ma_uint32 frameCount);

int initialize_playback_device(int channelCount, int sampleRate, ma_data_callback dataCallback);

int set_playback_buffer(void *buffer, long long int sizeInBytes);

void uninitialize_playback_device();

int start_playback();

void stop_playback();

int initialize_recording(long long int sizeInBytes, int channelCount, int sampleRate);

void uninitialize_recording();

void* stop_recording(long long int sizeInBytes);

int start_recording();

#endif // MIKRO_AUDIO_H