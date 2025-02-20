package pl.lemanski.mikroaudio.internal;

import com.v7878.foreign.*;

import java.lang.invoke.MethodHandle;

import static pl.lemanski.mikroaudio.internal.NativeTestHelper.*;

public class AAudio {

    AAudio() {
        // Should not be called directly
    }

    static {
        System.loadLibrary("aaudio");
    }

    final static Linker abi = Linker.nativeLinker();

    final static MethodHandle AAudio_createStreamBuilder = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudio_createStreamBuilder").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static MemorySegment AAudio_createStreamBuilder() throws Throwable {
        try (var arena = Arena.ofConfined()) {
            MemorySegment ptr = arena.allocate(C_POINTER);
            int result = (int) AAudio_createStreamBuilder.invokeExact(ptr);
            if (result != 0) {
                throw new RuntimeException("Failed to create AAudio stream builder: " + result);
            }
            return ptr.asReadOnly();
        }
    }
}
