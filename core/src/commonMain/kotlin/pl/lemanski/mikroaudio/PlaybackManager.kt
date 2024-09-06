package pl.lemanski.mikroaudio

interface PlaybackManager : AutoCloseable {
    fun setupPlayback(buffer: ByteArray)
    fun startPlayback()
    fun stopPlayback()
}

internal expect fun getPlaybackManager(): PlaybackManager