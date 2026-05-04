package com.pao.laboratory09.exercise2;

public enum Status {
    PENDING(0),
    PROCESSED(1),
    REJECTED(2);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Status fromValue(int value) {
        for (Status s : Status.values()) {
            if (s.value == value) return s;
        }
        return PENDING;
    }

    public static Status fromString(String name) {
        return Status.valueOf(name.toUpperCase());
    }
}
