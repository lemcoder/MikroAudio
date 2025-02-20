package pl.lemanski.mikroaudio.internal

import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import kotlinx.coroutines.*
import pl.lemanski.mikroaudio.Format

internal actual fun getPlaybackManager(channelCount: Int, sampleRate: Int, format: Format): PlaybackManager = AndroidPlaybackManager(channelCount, sampleRate, format)

internal class AndroidPlaybackManager(
    private val channelCount: Int,
    private val sampleRate: Int,
    private val format: Format
) : PlaybackManager {
    private var audioTrack: AudioTrack? = null
    private var playbackCallback: PlaybackManager.PlaybackCallback? = null
    private var playbackScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var playbackJob: Job = Job()

    override fun startPlayback() {
        if (audioTrack != null) {
            audioTrack?.release()
        }

        if (playbackCallback == null) {
            throw IllegalStateException("Playback callback not set")
        }

        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, getChannelConfig(), ENCODING_PCM_16BIT) // FIXME support other formats

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            getChannelConfig(),
            ENCODING_PCM_16BIT, // FIXME support other formats
            minBufferSize,
            AudioTrack.MODE_STREAM
        )

        audioTrack?.play()

        playbackJob = playbackScope.launch {
            val bytes = minBufferSize.toUInt()
            while (true) {
                val audioData = playbackCallback?.invoke(bytes) ?: throw IllegalStateException("Playback callback returned null")
                audioTrack?.write(audioData, 0, audioData.size)
                // yield()
            }
        }
    }

    override fun stopPlayback() {
        playbackJob.cancel()
        audioTrack?.stop()
    }

    override fun setCallback(callback: PlaybackManager.PlaybackCallback) {
        this.playbackCallback = callback
    }

    override fun close() {
        audioTrack?.release()
    }

    private fun getChannelConfig(): Int {
        return if (channelCount == 1) AudioFormat.CHANNEL_OUT_MONO
        else AudioFormat.CHANNEL_OUT_STEREO
    }
}
