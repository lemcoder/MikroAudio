package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import mikroAudio.*

internal actual fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager {
    return RecordManagerImpl(channelCount, sampleRate)
}

@OptIn(ExperimentalForeignApi::class)
internal class RecordManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int
) : RecordManager {
    private var bufferSize = -1

    override fun setupRecording(bufferSize: Long) {
        initialize_recording(bufferSize, channelCount, sampleRate)
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