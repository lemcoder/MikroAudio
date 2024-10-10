package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.AudioEngine

class DefaultAudioEngine(
    override val options: AudioEngine.Options = DefaultAudioEngineOptions
) : AudioEngine {

    private val playbackManager = getPlaybackManager(options.channelCount, options.sampleRate)
    private val recordManager = getRecordManager(options.channelCount, options.sampleRate)

    override fun setupRecording(bufferSize: Long) {
        recordManager.setupRecording(bufferSize)
    }

    override fun startRecording() {
        recordManager.startRecording()
    }

    override fun stopRecording(): ByteArray {
        return recordManager.stopRecording()
    }

    override fun setupPlayback(buffer: ByteArray) {
        playbackManager.setupPlayback(buffer)
    }

    override fun startPlayback() {
        playbackManager.startPlayback()
    }

    override fun stopPlayback() {
        playbackManager.stopPlayback()
    }
}