package pl.lemanski.mikroaudio

object MikroAudio {
    private val recordManager: RecordManager = getRecordManager()
    private val playbackManager: PlaybackManager = getPlaybackManager()

    fun record(bufferSize: Long) {
        recordManager.setupRecording(bufferSize)
        recordManager.startRecording()
    }

    fun stopRecording(): ByteArray {
        return recordManager.stopRecording()
    }

    fun playback(buffer: ByteArray) {
        playbackManager.setupPlayback(buffer)
        playbackManager.startPlayback()
    }

    fun stopPlayback() {
        playbackManager.stopPlayback()
    }
}