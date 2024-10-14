package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.PlaybackException

fun launchNative(vararg args: String, block: () -> Int) {
    val result = block()
    if (result != 0) {
        throw PlaybackException("Playback failed on ${args.joinToString()} Native error: $result")
    }
}