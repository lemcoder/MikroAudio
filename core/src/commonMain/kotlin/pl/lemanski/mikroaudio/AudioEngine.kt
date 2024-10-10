package pl.lemanski.mikroaudio

interface AudioEngine {
    interface Options {
        val channelCount: Int
        val sampleRate: Int
    }

    val options: Options

    fun setupRecording(bufferSize: Long)
    fun startRecording()

    fun stopRecording(): ByteArray

    fun setupPlayback(buffer: ByteArray)
    fun startPlayback()

    fun stopPlayback()
}