import pl.lemanski.mikroaudio.MikroAudio
import platform.posix.sleep
import kotlin.math.PI
import kotlin.math.sin

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
    val audio = MikroAudio()

    generateSinWave(44100).let { bytes ->
        audio.playback(callback = { sizeInBytes ->
            if (sizeInBytes <= Int.MAX_VALUE.toUInt()) {
                bytes.toByteArrayLittleEndian().copyOf(sizeInBytes.toInt())
            } else {
                throw IndexOutOfBoundsException("Size of the buffer is too big")
            }
        })
    }

    sleep(5u)
}