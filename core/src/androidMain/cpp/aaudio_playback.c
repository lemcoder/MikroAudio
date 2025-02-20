//
// Created by Mikolaj on 20.02.2025.
//

#include "aaudio/AAudio.h"

int createPlaybackStream() {
    AAudioStreamBuilder *builder;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    if (result != AAUDIO_OK) {
        return -1;
    }
}


