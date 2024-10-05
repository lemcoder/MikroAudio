package pl.lemanski.mikroaudio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

internal actual fun getPlaybackManager(): PlaybackManager = AndroidPlaybackManager()


internal class AndroidPlaybackManager : PlaybackManager {

    private var audioTrack: AudioTrack? = null
    private var audioBuffer: ByteArray? = null
    private var isPlaying = false

    override fun setupPlayback(buffer: ByteArray) {
        audioBuffer = buffer

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,  // Use music stream for playback
            44100,                      // Sample rate (44.1 kHz, common for music)
            AudioFormat.CHANNEL_OUT_MONO,  // Mono output (can be changed to CHANNEL_OUT_STEREO for stereo)
            AudioFormat.ENCODING_PCM_FLOAT, // Float encoding
            buffer.size,                 // Buffer size equal to the size of the input buffer
            AudioTrack.MODE_STATIC       // Use static mode since the buffer won't change
        ).apply {
            // Write the buffer to AudioTrack
            write(buffer, 0, buffer.size)
        }
    }

    // Start playback
    override fun startPlayback() {
        if (audioTrack == null || audioBuffer == null) {
            throw IllegalStateException("Playback has not been set up. Call setupPlayback() first.")
        }
        if (!isPlaying) {
            audioTrack?.play()
            isPlaying = true
        }
    }

    // Stop playback
    override fun stopPlayback() {
        if (isPlaying) {
            audioTrack?.stop()
            isPlaying = false
        }
    }

    // Close and release resources
    override fun close() {
        stopPlayback() // Ensure playback is stopped
        audioTrack?.release() // Release AudioTrack resources
        audioTrack = null
        audioBuffer = null
    }
}
