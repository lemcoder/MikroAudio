package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.Format

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int, format: Format): PlaybackManager = AndroidPlaybackManager(channelCount, sampleRate, format)

internal class AndroidPlaybackManager(
    private val channelCount: Int,
    private val sampleRate: Int,
    private val format: Format
) : PlaybackManager {
    init {
        System.loadLibrary("mikroAudioJNI")
    }

    override fun startPlayback() = launchNative("start_playback") {
        startPlaybackNative()
    }

    override fun stopPlayback() = launchNative("stop_playback") {
        stopPlaybackNative()
        return@launchNative 0
    }

    override fun close() = launchNative("uninitialize_playback_device") {
        uninitializePlaybackNative()
        return@launchNative 0
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        TODO("Not yet implemented")
    }

    private external fun initializePlaybackNative(channelCount: Int, sampleRate: Int): Int

    private external fun setPlaybackBufferNative(buffer: ByteArray, size: Int): Int

    private external fun startPlaybackNative(): Int

    private external fun stopPlaybackNative()

    private external fun uninitializePlaybackNative()
}
