package com.example.Job_Post.enumerator;

public enum TokenType {
    REFRESH,
    ACCESS;

    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }
}

