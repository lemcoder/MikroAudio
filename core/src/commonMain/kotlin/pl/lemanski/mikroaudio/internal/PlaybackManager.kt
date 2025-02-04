package pl.lemanski.mikroaudio.internal

interface PlaybackManager : AutoCloseable {
    fun interface PlaybackCallback {
        operator fun invoke(bytes: UInt): ByteArray
    }

    fun startPlayback()
    fun stopPlayback()
    fun setCallback(callback: PlaybackCallback)
}

internal expect fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager