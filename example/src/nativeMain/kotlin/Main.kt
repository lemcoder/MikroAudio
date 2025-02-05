import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import pl.lemanski.mikroaudio.AudioEngine
import pl.lemanski.mikroaudio.Format
import pl.lemanski.mikroaudio.MikroAudio
import pl.lemanski.mikroaudio.internal.DefaultAudioEngine
import platform.posix.sleep
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.assertTrue

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

fun main() {
    val audio = MikroAudio(
        audioEngine = DefaultAudioEngine(
            options = object : AudioEngine.Options {
                override val channelCount: Int = 2
                override val sampleRate: Int = 44_100
                override val format: Format = Format.S16
            }
        )
    )

    val wavFile = Path("C:\\Users\\Mikolaj\\Desktop\\midi\\sample.wav")
    val wavBytes = SystemFileSystem.source(wavFile).buffered().readByteArray()
    val wavNoHeader = wavBytes.copyOfRange(44, wavBytes.size)

    println(wavNoHeader.size)
    assertTrue { wavNoHeader.isNotEmpty() }

    val sinWave = generateSinWave(1024)

    wavNoHeader.let { bytes ->
        var pos = 0

        audio.playback(callback = { sizeInBytes ->
            pos += sizeInBytes.toInt()
            if (pos > bytes.size) {
                pos = sizeInBytes.toInt()
            }

            bytes.copyOfRange(pos - sizeInBytes.toInt(), pos)
        })
    }

    sleep(30u)
}