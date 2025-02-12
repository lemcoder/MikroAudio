package pl.lemanski.mikroaudio.internal

internal actual fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager = AndroidRecordManager(channelCount, sampleRate)

internal class AndroidRecordManager(
    private val channelCount: Int,
    private val sampleRate: Int
) : RecordManager {
    private var lastBufferSize: Long = 0

    override fun setupRecording(bufferSize: Long) = launchNative("initialize_recording: $channelCount, $sampleRate, $bufferSize") {
        return@launchNative 0
    }

    override fun startRecording() = launchNative("start_recording") {
        return@launchNative 0
    }

    override fun stopRecording(): ByteArray {
        return ByteArray(0)
    }

    override fun close() = launchNative("uninitialize_recording") {
        return@launchNative 0
    }
}
