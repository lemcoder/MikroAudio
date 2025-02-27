package pl.lemanski.mikroaudio.internal;

public enum AAudioStreamState {
    /**
     * The stream is created but not initialized yet.
     */
    UNINITIALIZED(0),
    /**
     * The stream is in an unrecognized state.
     */
    UNKNOWN(1),

    /**
     * The stream is open and ready to use.
     */
    OPEN(2),
    /**
     * The stream is just starting up.
     */
    STARTING(3),
    /**
     * The stream has started.
     */
    STARTED(4),
    /**
     * The stream is pausing.
     */
    PAUSING(5),
    /**
     * The stream has paused, could be restarted or flushed.
     */
    PAUSED(6),
    /**
     * The stream is being flushed.
     */
    FLUSHING(7),
    /**
     * The stream is flushed, ready to be restarted.
     */
    FLUSHED(8),
    /**
     * The stream is stopping.
     */
    STOPPING(9),
    /**
     * The stream has been stopped.
     */
    STOPPED(10),
    /**
     * The stream is closing.
     */
    CLOSING(11),
    /**
     * The stream has been closed.
     */
    CLOSED(12),
    /**
     * The stream is disconnected from audio device.
     * @deprecated
     */
    @Deprecated
    DISCONNECTED(13);

    private final int value;

    AAudioStreamState(int value) {
        this.value = value;
    }

    public static AAudioStreamState fromValue(int i) {
        for (AAudioStreamState state : AAudioStreamState.values()) {
            if (state.getValue() == i) {
                return state;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}