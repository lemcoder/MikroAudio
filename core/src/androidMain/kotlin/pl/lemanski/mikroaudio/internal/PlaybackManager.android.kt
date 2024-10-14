package pl.lemanski.mikroaudio.internal

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager = AndroidPlaybackManager(channelCount, sampleRate)

internal class AndroidPlaybackManager(
    private val channelCount: Int,
    private val sampleRate: Int
) : PlaybackManager {
    init {
        System.loadLibrary("mikroAudioJNI")
    }

    override fun setupPlayback(buffer: ByteArray) = launchNative("initialize_playback_device: $channelCount, $sampleRate", "set_playback_buffer: $buffer") {
        initializePlaybackNative(channelCount, sampleRate)
        setPlaybackBufferNative(buffer, buffer.size)
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

    private external fun initializePlaybackNative(channelCount: Int, sampleRate: Int): Int

    private external fun setPlaybackBufferNative(buffer: ByteArray, size: Int): Int

    private external fun startPlaybackNative(): Int

    private external fun stopPlaybackNative()

    private external fun uninitializePlaybackNative()
}
