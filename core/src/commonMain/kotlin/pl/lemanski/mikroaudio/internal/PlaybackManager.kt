package pl.lemanski.mikroaudio.internal

interface PlaybackManager : AutoCloseable {
    fun setupPlayback(buffer: ByteArray)
    fun startPlayback()
    fun stopPlayback()
}

internal expect fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager