package io.lemcoder.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.lemcoder.androidapp.ui.theme.MikroAudioTheme
import pl.lemanski.mikroaudio.MikroAudio
import kotlin.experimental.and
import kotlin.math.PI
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        playAudio()

        setContent {
            MikroAudioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
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

fun ShortArray.toByteArrayLittleEndian(): ByteArray {
    val byteArray = ByteArray(size * Short.SIZE_BYTES)

    for (i in indices) {
        byteArray[i * Short.SIZE_BYTES] = (this[i] and 0xFF).toByte()
        byteArray[i * Short.SIZE_BYTES + 1] = ((this[i].toInt() shr 8) and 0xFF).toByte()
    }

    return byteArray
}

fun playAudio() {
    MikroAudio().playback { size ->
        val frameOut = generateSinWave(size.toInt())
        convertPCM32ToPCM16(frameOut).toByteArrayLittleEndian()
    }
}

private fun convertPCM32ToPCM16(f32Data: FloatArray): ShortArray = ShortArray(f32Data.size) { i ->
    (f32Data[i].coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MikroAudioTheme {
        Greeting("Android")
    }
}