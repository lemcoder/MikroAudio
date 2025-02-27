package pl.lemanski.mikroaudio.internal;

import com.v7878.foreign.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static java.sql.DriverManager.println;
import static pl.lemanski.mikroaudio.internal.NativeTestHelper.C_INT;
import static pl.lemanski.mikroaudio.internal.NativeTestHelper.C_POINTER;

public class AAudio {

    AAudio() {
        // Should not be called directly
    }

    static {
        System.loadLibrary("aaudio");
    }

    private final static Arena libArena = Arena.ofConfined();
    private final static Linker abi = Linker.nativeLinker();

    // <------------------------------------------------------------->
    private final static MethodHandle AAudio_createStreamBuilder = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudio_createStreamBuilder").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static MemorySegment AAudioCreateStreamBuilder() throws Throwable {
        MemorySegment ptr = libArena.allocate(C_POINTER);
        int result = (int) AAudio_createStreamBuilder.invokeExact(ptr);
        if (result != AAudioResult.OK.getValue()) {
            throw new RuntimeException("Failed to create AAudio stream builder: " + result);
        }
        return ptr;
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setFormat = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setFormat").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    static void AAudioStreamBuilderSetFormat(MemorySegment builder, AAudioFormat format) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setFormat.invokeExact(pBuilder, format.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setSampleRate = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setSampleRate").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    static void AAudioStreamBuilderSetSampleRate(MemorySegment builder, int sampleRate) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setSampleRate.invokeExact(pBuilder, sampleRate);
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setChannelCount = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setChannelCount").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    static void AAudioStreamBuilderSetChannelCount(MemorySegment builder, int channelCount) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setChannelCount.invokeExact(pBuilder, channelCount);
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setDirection = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setDirection").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    static void AAudioStreamBuilderSetDirection(MemorySegment builder, AAudioAudioDirection direction) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setDirection.invokeExact(pBuilder, direction.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStreamBuilder_setPerformanceMode = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setPerformanceMode").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    static void AAudioStreamBuilderSetPerformanceMode(MemorySegment builder, AAudioPerformanceMode mode) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setPerformanceMode.invokeExact(pBuilder, mode.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setDataCallback = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setDataCallback").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER, C_POINTER)
    );

    public static int callback() {
        System.out.println("Callback called");
        return 0;
    }

    // TODO: Implement callback
    static void AAudioStreamBuilderSetDataCallback(MemorySegment builder, AAudioStreamDataCallbackApi callback) throws Throwable {
        MethodHandle onCallback = MethodHandles.lookup().findStatic(AAudio.class, "callback", MethodType.methodType(int.class));
        // Create a stub as a native symbol to be passed into native function.
        AAudioStreamDataCallbackInternal callbackInternal = (stream, userData, audioData, numFrames) -> {
            if (audioData == null || audioData.equals(MemorySegment.NULL) || audioData.byteSize() == 0) {
                System.err.println("Error: Invalid or empty audioData buffer!");
                return AAudioCallbackResult.CONTINUE.getValue(); // Stop if buffer is unusable
            }

            int numChannels = 2; // Change based on your stream format
            int bytesPerSample = Float.BYTES; // Assuming 32-bit float PCM
            int expectedSize = numFrames * numChannels * bytesPerSample;

            byte[] data = callback.onData(numFrames);

            // Ensure we don't copy beyond available space
            int copySize = Math.min(data.length, expectedSize);

            System.out.println("Expected size: " + expectedSize + ", Data size: " + data.length + ", Copying: " + copySize);
            System.out.println("audioData size: " + audioData.byteSize() + ", Expected: " + expectedSize);


            // Copy only valid data
            MemorySegment.copy(data, 0, audioData, ValueLayout.JAVA_BYTE, 0, copySize);

            // Zero out any remaining space if needed
            if (copySize < expectedSize) {
                audioData.asSlice(copySize, expectedSize - copySize).fill((byte) 0);
            }

            return AAudioCallbackResult.CONTINUE.getValue();
        };

        MemorySegment pCallback = createCallbackPtr(callbackInternal);
        MemorySegment nullPtr = MemorySegment.ofAddress(0);

        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setDataCallback.invokeExact(pBuilder, pCallback, nullPtr);
    }

    private static MemorySegment createCallbackPtr(AAudioStreamDataCallbackInternal callback) throws Throwable {
        FunctionDescriptor callbackDescriptor = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,  // aaudio_data_callback_result_t is int
                C_POINTER,   // AAudioStream* (non-null)
                C_POINTER,  // void* userData (nullable)
                C_POINTER,   // void* audioData (non-null)
                ValueLayout.JAVA_INT   // int32_t numFrames
        );

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findVirtual(
                AAudioStreamDataCallbackInternal.class,
                "onData",
                MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class, int.class)
        );

        return abi.upcallStub(mh.bindTo(callback), callbackDescriptor, libArena);
    }

    // <------------------------------------------------------------->
    // STREAM METHODS
    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStreamBuilder_openStream = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_openStream").get(),
            FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER)
    );

    static MemorySegment AAudioStreamBuilderOpenStream(MemorySegment builder) throws Throwable {
        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());
        // allocate a pointer to store the stream
        MemorySegment ptr = libArena.allocate(C_POINTER);

        int result = (int) AAudioStreamBuilder_openStream.invokeExact(pBuilder, ptr);
        if (result != AAudioResult.OK.getValue()) {
            throw new RuntimeException("Failed to open stream: " + result);
        }
        return ptr;
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getFormat = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getFormat").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static AAudioFormat AAudioStreamGetFormat(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return AAudioFormat.fromValue((int) AAudioStream_getFormat.invokeExact(pStream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getChannelCount = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getChannelCount").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static int AAudioStreamGetChannelCount(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return (int) AAudioStream_getChannelCount.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareChannelCount = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getHardwareChannelCount").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static int AAudioStreamGetHardwareChannelCount(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return (int) AAudioStream_getHardwareChannelCount.invokeExact(pStream);
    }


    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDeviceId = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getDeviceId").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static int AAudioStreamGetDeviceId(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return (int) AAudioStream_getDeviceId.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDirection = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getDirection").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static AAudioAudioDirection AAudioStreamGetDirection(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return AAudioAudioDirection.fromValue((int) AAudioStream_getDirection.invokeExact(pStream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getPerformanceMode = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getPerformanceMode").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static AAudioPerformanceMode AAudioStreamGetPerformanceMode(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return AAudioPerformanceMode.fromValue((int) AAudioStream_getPerformanceMode.invokeExact(pStream));
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getSampleRate = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getSampleRate").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static int AAudioStreamGetSampleRate(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return (int) AAudioStream_getSampleRate.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareSampleRate = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getHardwareSampleRate").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static int AAudioStreamGetHardwareSampleRate(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return (int) AAudioStream_getHardwareSampleRate.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getState = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getState").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static AAudioStreamState AAudioStreamGetState(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return AAudioStreamState.fromValue((int) AAudioStream_getState.invokeExact(pStream));
    }

    // ============================================================
    // Stream Control
    // ============================================================


    private final static MethodHandle AAudioStream_requestStart = abi.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_requestStart").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    static AAudioResult AAudioStreamRequestStart(MemorySegment stream) throws Throwable {
        // dereference the pointer
        MemorySegment streamAddress = stream.get(ValueLayout.ADDRESS, 0);
        MemorySegment pStream = MemorySegment.ofAddress(streamAddress.address());

        return AAudioResult.fromValue((int) AAudioStream_requestStart.invokeExact(pStream));
    }
}
