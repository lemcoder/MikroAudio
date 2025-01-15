package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.*
import mikroAudio.*

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate)
}

@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int
) : PlaybackManager {

    private var playbackCallback: PlaybackManager.PlaybackCallback = PlaybackManager.PlaybackCallback { ByteArray(0) } // empty callback
    private var userData = StableRef.create(playbackCallback)

    @OptIn(ExperimentalStdlibApi::class)
    private val dataCallback = staticCFunction { device: CPointer<ma_device>?, out: COpaquePointer?, input: COpaquePointer?, frames: UInt ->
        val userData = device?.pointed?.pUserData?.reinterpret<IntVar>()

        if (userData != null) {
            // Read the integer stored in userData
            val number = userData.pointed.value
            println("User data received: $number") // Should print 420
        } else {
            println("User data is null")
        }
//.asStableRef<PlaybackManager.PlaybackCallback>()?.get()
//        val frameData = callback.onFrames(frames.toInt())
//        frameData.usePinned { pinned ->
//            ma_copy_pcm_frames(
//                out,
//                pinned.addressOf(0),
//                frames.toULong(),
//                ma_format_f32,
//                1u // TODO pass channelCount
//            )
//        }
    }

    init {
        initialize_playback_device(
            channelCount = channelCount,
            sampleRate = sampleRate,
            dataCallback = dataCallback,
            userData = userData.asCPointer()
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
        userData.dispose()
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        playbackCallback = callback
        userData.dispose()
        userData = StableRef.create(playbackCallback)
    }
}
