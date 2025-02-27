package pl.lemanski.mikroaudio.internal;

public enum AAudioCallbackResult {
    /**
     * Continue calling the callback.
     */
    CONTINUE(0),

    /**
     * Stop calling the callback.
     * The application will still need to call AAudioStream_requestPause()
     * or AAudioStream_requestStop().
     */
    STOP(1);

    private final int value;

    AAudioCallbackResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static AAudioCallbackResult fromValue(int value) {
        for (AAudioCallbackResult result : AAudioCallbackResult.values()) {
            if (result.getValue() == value) {
                return result;
            }
        }
        return null;
    }
}
