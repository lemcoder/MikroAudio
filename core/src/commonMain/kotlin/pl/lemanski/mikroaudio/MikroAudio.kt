package pl.lemanski.mikroaudio

import pl.lemanski.mikroaudio.internal.DefaultAudioEngine
import pl.lemanski.mikroaudio.internal.PlaybackManager

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

    fun playback(callback: PlaybackManager.PlaybackCallback) {
        audioEngine.setupPlayback(callback)
        audioEngine.startPlayback()
    }

    fun stopPlayback() {
        audioEngine.stopPlayback()
    }
}