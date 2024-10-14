package pl.lemanski.mikroaudio.internal

internal actual fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager = AndroidRecordManager(channelCount, sampleRate)

internal class AndroidRecordManager(
    private val channelCount: Int,
    private val sampleRate: Int
) : RecordManager {
    private var lastBufferSize: Long = 0

    override fun setupRecording(bufferSize: Long) = launchNative("initialize_recording: $channelCount, $sampleRate, $bufferSize") {
        val result = initializeRecordingNative(channelCount, sampleRate, bufferSize)

        if (result == 0) {
            lastBufferSize = bufferSize
        }

        result
    }

    override fun startRecording() = launchNative("start_recording") {
        startRecordingNative()
    }

    override fun stopRecording(): ByteArray = stopRecordingNative(lastBufferSize)

    override fun close() = launchNative("uninitialize_recording") {
        uninitializeRecordingNative()
        return@launchNative 0
    }

    private external fun initializeRecordingNative(channelCount: Int, sampleRate: Int, sizeInBytes: Long): Int

    private external fun startRecordingNative(): Int

    private external fun stopRecordingNative(sizeInBytes: Long): ByteArray

    private external fun uninitializeRecordingNative()
}
