package pl.lemanski.mikroaudio.internal;

public enum AAudioPerformanceMode {
    /**
     * No particular performance needs. Default.
     */
    NONE(10),

    /**
     * Extending battery life is more important than low latency.
     * This mode is not supported in input streams.
     * For input, mode NONE will be used if this is requested.
     */
    POWER_SAVING(11),

    /**
     * Reducing latency is more important than battery life.
     */
    LOW_LATENCY(12);

    private final int value;

    AAudioPerformanceMode(int value) {
        this.value = value;
    }

    public static AAudioPerformanceMode fromValue(int i) {
        for (AAudioPerformanceMode mode : AAudioPerformanceMode.values()) {
            if (mode.getValue() == i) {
                return mode;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
