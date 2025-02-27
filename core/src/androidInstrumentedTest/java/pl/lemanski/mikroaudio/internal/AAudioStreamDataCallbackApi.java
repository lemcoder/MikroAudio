package pl.lemanski.mikroaudio.internal;

import com.v7878.foreign.MemorySegment;

@FunctionalInterface
public interface AAudioStreamDataCallbackApi {
    byte[] onData(int numFrames);
}
