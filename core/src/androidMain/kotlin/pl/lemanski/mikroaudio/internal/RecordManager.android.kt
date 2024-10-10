package pl.lemanski.mikroaudio.internal

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

internal actual fun getRecordManager(channelCount: Int, sampleRate: Int): RecordManager = AndroidRecordManager(channelCount, sampleRate)

internal class AndroidRecordManager(
    private val channelCount: Int,
    private val sampleRate: Int
) : RecordManager {
    private var audioRecord: AudioRecord? = null
    private var bufferSize: Int = 0
    private var isRecording = false
    private lateinit var recordingBuffer: ByteArray

    @SuppressLint("MissingPermission")
    override fun setupRecording(bufferSize: Long) {
        val audioFormat = when (channelCount) {
            1 -> AudioFormat.CHANNEL_IN_MONO
            2 -> AudioFormat.CHANNEL_IN_STEREO
            else -> throw IllegalArgumentException("Unsupported channel count: $channelCount")
        }

        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            audioFormat,
            AudioFormat.ENCODING_PCM_FLOAT // Float encoding
        )

        this.bufferSize = minBufferSize.coerceAtLeast(bufferSize.toInt())

        recordingBuffer = ByteArray(this.bufferSize)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, // Use the device's microphone
            sampleRate,
            audioFormat,
            AudioFormat.ENCODING_PCM_FLOAT, // Float encoding
            this.bufferSize // Buffer size for the recording
        )
    }

    override fun startRecording() {
        if (audioRecord == null) {
            throw IllegalStateException("Recording has not been set up. Call setupRecording() first.")
        }

        audioRecord?.startRecording()
        isRecording = true

        //TODO add code here to record in a background thread and fill the buffer.
    }

    override fun stopRecording(): ByteArray {
        if (!isRecording) {
            throw IllegalStateException("Recording is not active. Call startRecording() first.")
        }

        audioRecord?.stop()
        isRecording = false

        audioRecord?.read(recordingBuffer, 0, recordingBuffer.size)

        return recordingBuffer
    }

    override fun close() {
        if (isRecording) {
            stopRecording() // Ensure recording is stopped
        }
        audioRecord?.release() // Release AudioRecord resources
        audioRecord = null
    }
}
