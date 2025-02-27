package pl.lemanski.mikroaudio.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.v7878.foreign.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.PI
import kotlin.math.sin


@RunWith(AndroidJUnit4::class)
class PlaybackManagerTest {

    fun FloatArray.toByteArrayLittleEndian(): ByteArray {
        val byteArray = ByteArray(size * Float.SIZE_BYTES)
        for (i in indices) {
            val intBits = this[i].toRawBits()
            byteArray[i * Float.SIZE_BYTES] = (intBits and 0xFF).toByte()
            byteArray[i * Float.SIZE_BYTES + 1] = ((intBits shr 8) and 0xFF).toByte()
            byteArray[i * Float.SIZE_BYTES + 2] = ((intBits shr 16) and 0xFF).toByte()
            byteArray[i * Float.SIZE_BYTES + 3] = ((intBits shr 24) and 0xFF).toByte()
        }
        return byteArray
    }


    fun generateSinWave(buffLen: Int, sampleRate: Int = 44100): FloatArray {
        val frameOut = FloatArray(buffLen)
        val amplitude = 0.5
        val frequency = 440.0
        val twoPi = 2.0 * PI
        var phase = 0.0

        for (i in 0 until buffLen) {
            frameOut[i] = (amplitude * sin(phase)).toFloat()
            phase += twoPi * frequency / sampleRate // Correct phase increment
            if (phase >= twoPi) {
                phase -= twoPi // Wrap phase to avoid overflow
            }
        }

        return frameOut
    }

    @Test
    fun test() {
        val builder = AAudio.AAudioCreateStreamBuilder()
        AAudio.AAudioStreamBuilderSetFormat(builder, AAudioFormat.PCM_FLOAT)
        AAudio.AAudioStreamBuilderSetSampleRate(builder, 44100)
        AAudio.AAudioStreamBuilderSetChannelCount(builder, 2)
        AAudio.AAudioStreamBuilderSetDirection(builder, AAudioAudioDirection.OUTPUT)
        AAudio.AAudioStreamBuilderSetPerformanceMode(builder, AAudioPerformanceMode.LOW_LATENCY)

        val precomputedAudio = generateSinWave(1024 * 10)
        val byteArray = precomputedAudio.toByteArrayLittleEndian()

        AAudio.AAudioStreamBuilderSetDataCallback(builder) { numFrames ->
            val numChannels = 2
            val byteSize: Int = numFrames * numChannels * java.lang.Float.BYTES

            return@AAudioStreamBuilderSetDataCallback byteArray.copyOf(byteSize) // Success
        }
        val stream = AAudio.AAudioStreamBuilderOpenStream(builder)

        val format = AAudio.AAudioStreamGetFormat(stream)
        val sampleRate = AAudio.AAudioStreamGetSampleRate(stream)
        val hwSampleRate = AAudio.AAudioStreamGetHardwareSampleRate(stream)
        val channelCount = AAudio.AAudioStreamGetChannelCount(stream)
        val hwChannelCount = AAudio.AAudioStreamGetHardwareChannelCount(stream)
        val perfMode = AAudio.AAudioStreamGetPerformanceMode(stream)
        val dir = AAudio.AAudioStreamGetDirection(stream)
        val state = AAudio.AAudioStreamGetState(stream)

        println(
            """
            format: $format
            sampleRate: $sampleRate
            hwSampleRate: $hwSampleRate
            channelCount: $channelCount
            hwChannelCount: $hwChannelCount
            perfMode: $perfMode
            dir: $dir
            state: $state
            """.trimIndent()
        )

        AAudio.AAudioStreamRequestStart(stream)
        Thread.sleep(1000)
    }
}