package pl.lemanski.mikroaudio

interface RecordManager : AutoCloseable {
    fun setupRecording(bufferSize: Long)
    fun startRecording()
    fun stopRecording(): ByteArray
}

internal expect fun getRecordManager(): RecordManager