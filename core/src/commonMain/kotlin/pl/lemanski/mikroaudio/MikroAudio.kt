package pl.lemanski.mikroaudio

import pl.lemanski.mikroaudio.internal.DefaultAudioEngine

class MikroAudio(
    private val audioEngine: AudioEngine = DefaultAudioEngine()
) {

    fun record(bufferSize: Long) {
        audioEngine.setupRecording(bufferSize)
        audioEngine.startRecording()
    }

    fun stopRecording(): ByteArray {
        return audioEngine.stopRecording()
    }

    fun playback(buffer: ByteArray) {
        audioEngine.setupPlayback(buffer)
        audioEngine.startPlayback()
    }

    fun stopPlayback() {
        audioEngine.stopPlayback()
    }
}