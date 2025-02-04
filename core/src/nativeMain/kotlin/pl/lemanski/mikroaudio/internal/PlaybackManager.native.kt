package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.*
import mikroAudio.*

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate)
}

/**
 * This is basically a global variable.
 */
private object CallbackHolder {
    var callback: PlaybackManager.PlaybackCallback? = null
    var channels: Int = 0
}

@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int
) : PlaybackManager {

    private val dataCallback = staticCFunction { device: CPointer<ma_device>?, out: COpaquePointer?, _: COpaquePointer?, frames: UInt ->
        val sizeInBytes = ma_get_bytes_per_frame(ma_format_f32, CallbackHolder.channels.toUInt()) * frames
        val bytes = CallbackHolder.callback?.invoke(sizeInBytes) ?: throw IllegalStateException("Callback not set")
        ma_copy_pcm_frames(out, bytes.refTo(0), frames.toULong(), ma_format_f32, CallbackHolder.channels.toUInt())
    }

    // TODO check what happens when we have multiple instances of PlaybackManagerImpl
    init {
        println("Created PlaybackManager")
        CallbackHolder.callback = null
        CallbackHolder.channels = channelCount
        initialize_playback_device(
            channelCount = channelCount,
            sampleRate = sampleRate,
            dataCallback = dataCallback,
            userData = null
        )
    }

    override fun startPlayback() {
        start_playback()
    }

    override fun stopPlayback() {
        stop_playback()
    }

    override fun close() {
        uninitialize_playback_device()
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        println("Callback set")
        CallbackHolder.callback = callback
    }
}
