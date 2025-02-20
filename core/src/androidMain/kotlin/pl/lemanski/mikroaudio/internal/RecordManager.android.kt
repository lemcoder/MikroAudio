package pl.lemanski.mikroaudio.internal

internal actual fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager = AndroidRecordManager(channelCount, sampleRate)

internal class AndroidRecordManager(
    private val channelCount: Int,
    private val sampleRate: Int
) : RecordManager {
    override fun setupRecording(bufferSize: Long) {
        TODO("Not yet implemented")
    }

    override fun startRecording() {
        TODO("Not yet implemented")
    }

    override fun stopRecording(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
