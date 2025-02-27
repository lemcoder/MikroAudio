package pl.lemanski.mikroaudio.internal;

import com.v7878.foreign.FunctionDescriptor;
import com.v7878.foreign.MemorySegment;
import com.v7878.foreign.ValueLayout;

@FunctionalInterface
interface AAudioStreamDataCallbackInternal {
    int onData(MemorySegment stream, MemorySegment userData, MemorySegment audioData, int numFrames);
}
