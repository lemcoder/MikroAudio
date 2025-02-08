package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.*
import mikroAudio.*
import pl.lemanski.mikroaudio.Format

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int, format: Format): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate, format)
}

@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int,
    private val format: Format
) : PlaybackManager {

    private var playbackDevice: CPointer<ma_device>? = null

    private val dataCallback = staticCFunction { device: CPointer<ma_device>?, out: COpaquePointer?, _: COpaquePointer?, frames: UInt ->
        val info = nativeHeap.alloc<ma_device_info>()
        ma_device_get_info(device, ma_device_type_playback, info.ptr)

        val nativeFormat = Settings.FORMAT
        val channelCount = Settings.CHANNEL_COUNT
        val sizeInBytes = ma_get_bytes_per_frame(nativeFormat, channelCount) * frames
        val bytes = Settings.CALLBACK?.invoke(sizeInBytes) ?: throw IllegalStateException("Callback not set")
        ma_copy_pcm_frames(out, bytes.refTo(0), frames.toULong(), nativeFormat, channelCount)
    }

    init {
        playbackDevice = nativeHeap.alloc<ma_device>().ptr
        val deviceConfig = ma_device_config_init(ma_device_type_playback)

        memScoped {
            val config = deviceConfig.getPointer(this).reinterpret<ma_device_config_flat>()

            config.pointed.playback.format = format.toNativeFormat()
            config.pointed.playback.channels = channelCount.toUInt()
            config.pointed.sampleRate = sampleRate.toUInt()
            config.pointed.dataCallback = this@PlaybackManagerImpl.dataCallback
            ma_device_init(null, config.reinterpret(), playbackDevice)
        }

        if (playbackDevice == null) {
            throw IllegalStateException("Failed to initialize playback device")
        }

        Settings.CHANNEL_COUNT = channelCount.toUInt()
        Settings.FORMAT = format.toNativeFormat()
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
        ma_device_uninit(playbackDevice)
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        println("Callback set")
        Settings.CALLBACK = callback
    }

    internal fun Format.toNativeFormat(): ma_format {
        return when (this) {
            Format.F32 -> ma_format_f32
            Format.S16 -> ma_format_s16
            Format.S24 -> ma_format_s24
            Format.S32 -> ma_format_s32
            Format.U8  -> ma_format_u8
        }
    }

    object Settings {
        var CHANNEL_COUNT = 2u
        var FORMAT = ma_format_f32
        var CALLBACK = null as PlaybackManager.PlaybackCallback?
    }
}
