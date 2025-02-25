package pl.lemanski.mikroaudio.internal;

public enum AAudioFormat {
    INVALID(-1),
    UNSPECIFIED(0),
    PCM_I16(1),
    PCM_FLOAT(2),
    PCM_I24_PACKED(3),
    PCM_I32(4),
    IEC61937(5);

    private final int value;

    AAudioFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioFormat fromValue(int value) {
        for (AAudioFormat format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
