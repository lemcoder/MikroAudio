package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.AudioEngine

internal object DefaultAudioEngineOptions : AudioEngine.Options {
    override var channelCount: Int = 1
        private set
    override var sampleRate: Int = 44_100
        private set
}