//
// Created by Mikolaj on 20.02.2025.
//

#include "aaudio/AAudio.h"

aaudio_data_callback_result_t myCallback(
        AAudioStream *stream,
        void *userData,
        void *audioData,
        int32_t numFrames) {
    int64_t timeout = 0;

    // Write samples directly into the audioData array.
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

int createPlaybackStream() {
    AAudioStreamBuilder *builder;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    if (result != AAUDIO_OK) {
        return -1;
    }

    // Set the stream parameters
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT); // or AAUDIO_FORMAT_PCM_I16
    AAudioStreamBuilder_setSampleRate(builder, 48000); // Set your desired sample rate
    AAudioStreamBuilder_setChannelCount(builder, 2); // Stereo
    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT); // Output stream for playback
    AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY); // Low latency mode
    AAudioStreamBuilder_setDataCallback(builder, myCallback, NULL); // Optional: Set a data callback
    AAudioStreamBuilder_setErrorCallback(builder, errorCallback, nullptr); // Optional: Set an error callback

    // Open the stream
    AAudioStream *stream;
    result = AAudioStreamBuilder_openStream(builder, &stream);
    AAudioStream_getFormat(stream); // Get the actual format
    if (result != AAUDIO_OK) {
        AAudioStreamBuilder_delete(builder);
        return -1;
    }

    // Clean up the builder
    AAudioStreamBuilder_delete(builder);

    // Start the stream
    result = AAudioStream_requestStart(stream);
    if (result != AAUDIO_OK) {
        AAudioStream_close(stream);
        return -1;
    }

    return 0; // Success
}


