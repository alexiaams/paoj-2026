package com.pao.laboratory03.bonus;

public class InvalidTransitionException extends RuntimeException {
    private final Status fromStatus;
    private final Status toStatus;

    public InvalidTransitionException(Status fromStatus, Status toStatus) {
        super("Nu se poate trece din " + fromStatus + " in " + toStatus);
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public Status getFromStatus() {
        return fromStatus;
    }

    public Status getToStatus() {
        return toStatus;
    }
}
