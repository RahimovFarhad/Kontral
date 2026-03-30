package com.example.Job_Post.enumerator;

public enum PostType {
    JOB_REQUEST,
    SERVICE_OFFER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
