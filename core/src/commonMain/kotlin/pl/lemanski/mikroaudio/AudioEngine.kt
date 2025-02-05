package pl.lemanski.mikroaudio

import pl.lemanski.mikroaudio.internal.PlaybackManager

interface AudioEngine {
    interface Options {
        val channelCount: Int
        val sampleRate: Int
        val format: Format
    }

    val options: Options

    fun setupRecording(bufferSize: Long)
    fun startRecording()

    fun stopRecording(): ByteArray

    fun setupPlayback(callback: PlaybackManager.PlaybackCallback)
    fun startPlayback()

    fun stopPlayback()
}