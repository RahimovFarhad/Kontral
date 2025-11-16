package com.example.Job_Post.enumerator;

public enum NegotiationStatus {
    ACTIVE,
    ACCEPTED,
    REJECTED,
    CLOSED;

    public static NegotiationStatus fromString(String status) {
        for (NegotiationStatus s : NegotiationStatus.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }

    public static String toString(NegotiationStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

}
