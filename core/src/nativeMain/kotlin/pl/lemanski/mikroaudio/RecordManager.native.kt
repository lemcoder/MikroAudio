package pl.lemanski.mikroaudio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import mikroAudio.*

internal actual fun getRecordManager(): RecordManager {
    return RecordManagerImpl()
}

@OptIn(ExperimentalForeignApi::class)
internal class RecordManagerImpl : RecordManager {
    private var bufferSize = -1

    override fun setupRecording(bufferSize: Long) {
        initialize_recording(bufferSize, 2, 44_100)
    }

    override fun startRecording() {
        start_recording()
    }

    override fun stopRecording(): ByteArray {
        if (bufferSize < 0) {
            return byteArrayOf()
        }

        return stop_recording(bufferSize.toLong())?.readBytes(bufferSize) ?: byteArrayOf()
    }

    override fun close() {
        uninitialize_recording()
    }
}