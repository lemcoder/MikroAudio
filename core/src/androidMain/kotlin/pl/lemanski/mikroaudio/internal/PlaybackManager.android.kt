package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.Format

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int, format: Format): PlaybackManager = AndroidPlaybackManager(channelCount, sampleRate, format)

internal class AndroidPlaybackManager(
    private val channelCount: Int,
    private val sampleRate: Int,
    private val format: Format
) : PlaybackManager {
    init {
        System.loadLibrary("ma")
    }

    override fun startPlayback() = launchNative("start_playback") {
        return@launchNative 0
    }

    override fun stopPlayback() = launchNative("stop_playback") {
        return@launchNative 0
    }

    override fun close() = launchNative("uninitialize_playback_device") {
        return@launchNative 0
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        TODO("Not yet implemented")
    }

}
