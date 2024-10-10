package pl.lemanski.mikroaudio.internal

interface RecordManager : AutoCloseable {
    fun setupRecording(bufferSize: Long)
    fun startRecording()
    fun stopRecording(): ByteArray
}

internal expect fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager