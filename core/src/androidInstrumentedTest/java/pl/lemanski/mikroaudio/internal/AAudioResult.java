package pl.lemanski.mikroaudio.internal;

public enum AAudioResult {
    /**
     * The call was successful.
     */
    OK(0),

    /**
     * Reserved. This should not be returned.
     */
    ERROR_BASE(-900),

    /**
     * The audio device was disconnected. This could occur, for example, when headphones
     * are plugged in or unplugged. The stream cannot be used after the device is disconnected.
     * Applications should stop and close the stream.
     * If this error is received in an error callback then another thread should be
     * used to stop and close the stream.
     */
    ERROR_DISCONNECTED(-899),

    /**
     * An invalid parameter was passed to AAudio.
     */
    ERROR_ILLEGAL_ARGUMENT(-898),
    // reserved

    /**
     * An internal error occurred.
     */
    ERROR_INTERNAL(-896),

    /**
     * The requested operation is not appropriate for the current state of AAudio.
     */
    ERROR_INVALID_STATE(-895),
    // reserved
    // reserved

    /**
     * The server rejected the handle used to identify the stream.
     */
    ERROR_INVALID_HANDLE(-892),
    // reserved

    /**
     * The function is not implemented for this stream.
     */
    ERROR_UNIMPLEMENTED(-890),

    /**
     * A resource or information is unavailable.
     * This could occur when an application tries to open too many streams,
     * or a timestamp is not available.
     */
    ERROR_UNAVAILABLE(-889),

    /**
     * Reserved. This should not be returned.
     */
    ERROR_NO_FREE_HANDLES(-888),

    /**
     * Memory could not be allocated.
     */
    ERROR_NO_MEMORY(-887),

    /**
     * A NULL pointer was passed to AAudio.
     * Or a NULL pointer was detected internally.
     */
    ERROR_NULL(-886),

    /**
     * An operation took longer than expected.
     */
    ERROR_TIMEOUT(-885),

    /**
     * A queue is full. This queue would be blocked.
     */
    ERROR_WOULD_BLOCK(-884),

    /**
     * The requested data format is not supported.
     */
    ERROR_INVALID_FORMAT(-883),

    /**
     * A requested was out of range.
     */
    ERROR_OUT_OF_RANGE(-882),

    /**
     * The audio service was not available.
     */
    ERROR_NO_SERVICE(-881),

    /**
     * The requested sample rate was not supported.
     */
    ERROR_INVALID_RATE(-880);

    private final int value;

    AAudioResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}