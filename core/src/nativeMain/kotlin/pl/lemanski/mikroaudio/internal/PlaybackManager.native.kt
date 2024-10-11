package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toCValues
import mikroAudio.*

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate)
}

@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int
) : PlaybackManager {
    init {
        initialize_playback_device(channelCount, sampleRate)
    }

    override fun setupPlayback(buffer: ByteArray) {
        set_playback_buffer(buffer.toCValues(), buffer.size.toLong())
    }

    override fun startPlayback() {
        start_playback()
    }

    override fun stopPlayback() {
        stop_playback()
    }

    override fun close() {
        uninitialize_playback_device()
    }
}