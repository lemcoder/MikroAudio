package pl.lemanski.mikroaudio.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.v7878.foreign.*
import org.junit.Test
import org.junit.runner.RunWith
import pl.lemanski.mikroaudio.internal.NativeTestHelper.C_INT
import java.lang.invoke.MethodHandle

@RunWith(AndroidJUnit4::class)
class PlaybackManagerTest {
    val linker = Linker.nativeLinker()

    @Test
    fun testExecvp() {
        // System.loadLibrary("c")  // Load libc
        System.loadLibrary("mikroaudio")  // Load libc
    }
}