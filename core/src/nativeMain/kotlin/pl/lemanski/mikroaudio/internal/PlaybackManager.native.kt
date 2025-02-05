package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.*
import mikroAudio.*
import pl.lemanski.mikroaudio.Format

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int, format: Format): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate, format)
}

/**
 * This is basically a global variable.
 * TODO map this to struct and pass as user data to ma_device_config
 */
private object CallbackHolder {
    var callback: PlaybackManager.PlaybackCallback? = null
    var channelCount: Int = 0
    var sampleRate: Int = 0
    var format: Format = Format.F32

    @OptIn(ExperimentalForeignApi::class)
    fun toNativeFormat(format: Format): ma_format {
        return when (format) {
            Format.F32 -> ma_format_f32
            Format.S16 -> ma_format_s16
            Format.S24 -> ma_format_s24
            Format.S32 -> ma_format_s32
            Format.U8  -> ma_format_u8
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int,
    private val format: Format
) : PlaybackManager {

    private var playbackDevice: CPointer<ma_device>? = null

    private val dataCallback = staticCFunction { device: CPointer<ma_device>?, out: COpaquePointer?, _: COpaquePointer?, frames: UInt ->
        val nativeFormat = CallbackHolder.toNativeFormat(CallbackHolder.format)

        println(CallbackHolder.format.name)

        val sizeInBytes = ma_get_bytes_per_frame(nativeFormat, CallbackHolder.channelCount.toUInt()) * frames
        val bytes = CallbackHolder.callback?.invoke(sizeInBytes) ?: throw IllegalStateException("Callback not set")
        ma_copy_pcm_frames(out, bytes.refTo(0), frames.toULong(), nativeFormat, CallbackHolder.channelCount.toUInt())
    }

    // TODO check what happens when we have multiple instances of PlaybackManagerImpl
    init {
        CallbackHolder.callback = null
        initializeCallbackHolder()
        playbackDevice = initialize_playback_device(channelCount, sampleRate, dataCallback, CallbackHolder.toNativeFormat(format), null)
        if (playbackDevice == null) {
            throw IllegalStateException("Failed to initialize playback device")
        }
    }

    override fun startPlayback() {
        val result = ma_device_start(playbackDevice)
        if (result != MA_SUCCESS) {
            throw IllegalStateException("Failed to start playback")
        }
    }

    override fun stopPlayback() {
        if (playbackDevice == null) {
            return
        }

        ma_device_stop(playbackDevice)
    }

    override fun close() {
        uninitialize_playback_device(playbackDevice)
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        println("Callback set")
        CallbackHolder.callback = callback
    }

    internal fun initializeCallbackHolder() {
        CallbackHolder.channelCount = channelCount
        CallbackHolder.sampleRate = sampleRate
        CallbackHolder.format = format
    }
}
