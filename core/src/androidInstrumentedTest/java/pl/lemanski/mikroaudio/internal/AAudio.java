package pl.lemanski.mikroaudio.internal;

import com.v7878.foreign.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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
    static void AAudioStreamBuilderSetDataCallback(MemorySegment builder) throws Throwable {
        MethodHandle onCallback = MethodHandles.lookup().findStatic(AAudio.class, "callback", MethodType.methodType(int.class));
        // Create a stub as a native symbol to be passed into native function.
        // void (*ptr)()
        MemorySegment pCallback = abi.upcallStub(onCallback, FunctionDescriptor.of(C_INT), libArena);
        MemorySegment nullPtr = MemorySegment.ofAddress(0);

        // dereference the pointer
        MemorySegment builderAddress = builder.get(ValueLayout.ADDRESS, 0);
        MemorySegment pBuilder = MemorySegment.ofAddress(builderAddress.address());

        AAudioStreamBuilder_setDataCallback.invokeExact(pBuilder, pCallback, nullPtr);
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
}
