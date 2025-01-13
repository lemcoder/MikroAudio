package pl.lemanski.mikroaudio.internal

import kotlinx.cinterop.*
import mikroAudio.*

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int): PlaybackManager {
    return PlaybackManagerImpl(channelCount, sampleRate)
}

@OptIn(ExperimentalForeignApi::class)
typealias CVoidPointer = CPointer<out CPointed>?

//static void playback_data_callback(ma_device *pDevice, void *pOutput, const void *pInput, ma_uint32 frameCount) {
//    if (!pPlaybackBuffer || playbackBufferSize == 0 || playbackBytesPerFrame == 0) {
//        return;
//    }
//
//    ma_uint64 byteCount = frameCount * playbackBytesPerFrame;
//    ma_uint32 bytesToPlay;
//
//    if (playbackCursor >= playbackBufferSize) {
//        playbackCursor = 0;  // Loop playback by resetting the cursor
//    }
//
//    if (playbackCursor + byteCount > playbackBufferSize) {
//        bytesToPlay = playbackBufferSize - playbackCursor;
//        frameCount = bytesToPlay / playbackBytesPerFrame;
//    } else {
//        bytesToPlay = byteCount;
//    }
//
//    if (bytesToPlay > 0) {
//        ma_copy_pcm_frames(pOutput, pPlaybackBuffer + playbackCursor, frameCount, pDevice->playback.format, pDevice->playback.channels);
//        playbackCursor += bytesToPlay;
//    }
//
//    (void)pInput;  // Unused parameter
//}


@OptIn(ExperimentalForeignApi::class)
internal class PlaybackManagerImpl(
    private val channelCount: Int,
    private val sampleRate: Int
) : PlaybackManager {

    private val callback = staticCFunction { device: CPointer<ma_device>?, out: CVoidPointer, input: CVoidPointer, frames: UInt ->
        println(frames)
    }

    init {
        initialize_playback_device(channelCount, sampleRate, callback)
    }

    override fun setupPlayback(buffer: ByteArray) {
        set_playback_buffer(buffer.toCValues(), buffer.size.toLong())
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
}