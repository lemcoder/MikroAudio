package pl.lemanski.mikroaudio.internal

import pl.lemanski.mikroaudio.AudioEngine
import pl.lemanski.mikroaudio.Format

internal object DefaultAudioEngineOptions : AudioEngine.Options {
    override var channelCount: Int = 1
        private set
    override var sampleRate: Int = 44_100
        private set
    override var format: Format = Format.F32
        private set
}