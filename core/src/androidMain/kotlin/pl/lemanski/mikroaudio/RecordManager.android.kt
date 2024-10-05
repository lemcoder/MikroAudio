package pl.lemanski.mikroaudio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

internal actual fun getRecordManager(): RecordManager = AndroidRecordManager()

internal class AndroidRecordManager : RecordManager {
    private var audioRecord: AudioRecord? = null
    private var bufferSize: Int = 0
    private var isRecording = false
    private lateinit var recordingBuffer: ByteArray

    // Setup the AudioRecord with the desired buffer size
    @SuppressLint("MissingPermission")
    override fun setupRecording(bufferSize: Long) {
        // Calculate the minimum buffer size required for audio recording
        val minBufferSize = AudioRecord.getMinBufferSize(
            44100, // Sample rate (44.1 kHz)
            AudioFormat.CHANNEL_IN_MONO, // Mono input (can change to CHANNEL_IN_STEREO if needed)
            AudioFormat.ENCODING_PCM_FLOAT // Float encoding
        )

        // Ensure the provided bufferSize is greater than the minimum required
        this.bufferSize = minBufferSize.coerceAtLeast(bufferSize.toInt())

        // Initialize the recording buffer with the specified buffer size
        recordingBuffer = ByteArray(this.bufferSize)

        // Create an AudioRecord instance
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, // Use the device's microphone
            44100, // Sample rate (44.1 kHz)
            AudioFormat.CHANNEL_IN_MONO, // Mono input
            AudioFormat.ENCODING_PCM_16BIT, // 16-bit PCM encoding
            this.bufferSize // Buffer size for the recording
        )
    }

    // Start recording audio
    override fun startRecording() {
        if (audioRecord == null) {
            throw IllegalStateException("Recording has not been set up. Call setupRecording() first.")
        }

        // Start recording
        audioRecord?.startRecording()
        isRecording = true

        // You could add code here to record in a background thread and fill the buffer, if necessary.
    }

    // Stop recording and return the recorded audio as a ByteArray
    override fun stopRecording(): ByteArray {
        if (!isRecording) {
            throw IllegalStateException("Recording is not active. Call startRecording() first.")
        }

        // Stop recording
        audioRecord?.stop()
        isRecording = false

        // Read recorded data from the AudioRecord buffer
        audioRecord?.read(recordingBuffer, 0, recordingBuffer.size)

        // Return the recorded audio as a ByteArray
        return recordingBuffer
    }

    // Close the AudioRecord and release resources
    override fun close() {
        if (isRecording) {
            stopRecording() // Ensure recording is stopped
        }
        audioRecord?.release() // Release AudioRecord resources
        audioRecord = null
    }
}
