package pl.lemanski.mikroaudio.internal;

public enum AAudioDataCallbackResult {
    /**
     * Continue calling the callback.
     */
    CONTINUE(0),

    /**
     * Stop calling the callback.
     *
     * The application will still need to call AAudioStream_requestPause()
     * or AAudioStream_requestStop().
     */
    STOP(1);

    private final int value;

    AAudioDataCallbackResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}