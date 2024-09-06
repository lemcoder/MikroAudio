#ifndef PANDALOOP_AUDIOPLAYER_H
#define PANDALOOP_AUDIOPLAYER_H

#include <string.h>
#include <stdlib.h>
#include "miniaudio/miniaudio.h"
#include <unistd.h>

int initialize_playback_device(int channelCount, int sampleRate);

int set_playback_buffer(void *buffer, long long int sizeInBytes);

void uninitialize_playback_device();

int start_playback();

void stop_playback();

int initialize_recording(long long int sizeInBytes, int channelCount, int sampleRate);

void uninitialize_recording();

void* stop_recording(long long int sizeInBytes);

int start_recording();

#endif // PANDALOOP_AUDIOPLAYER_H


#endif // PANDALOOP_AUDIOPLAYER_H