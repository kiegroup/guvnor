package org.drools.guvnor.shared.api;

public enum Valid {
    VALID,
    INVALID,
    UNDETERMINED;

    public static Valid fromString(String str) {
        if (str != null) {
            for (Valid value : values()) {
                if (str.equals(value.toString())) {
                    return value;
                }
            }
        }
        return UNDETERMINED;
    }

    public static Valid fromBoolean(boolean b) {
          return b ? VALID : INVALID;
    }


}
