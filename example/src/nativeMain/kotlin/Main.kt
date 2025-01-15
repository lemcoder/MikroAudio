import pl.lemanski.mikroaudio.MikroAudio
import platform.posix.atan
import platform.posix.sleep
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

fun generateSinWave(buffLen: Int): FloatArray {
    // simple sine wave generator
    val frameOut = FloatArray(buffLen)
    val amplitude = .01
    val frequency = 440
    val twopi: Double = 8.0 * atan(1.0)
    var phase: Double = 0.0

    for (i in 0 until buffLen) {
        frameOut[i] = (amplitude * sin(phase)).toFloat()
        phase += twopi * frequency / 44100
        if (phase > twopi) {
            phase -= twopi
        }
    }

    return frameOut
}

fun main() {
    val audio = MikroAudio()

    generateSinWave(44100).let { bytes ->

        audio.playback(onFrames = { frameCount ->
            bytes.toByteArrayLittleEndian().drop(frameCount).toByteArray()
        })
    }

    sleep(5u)
}