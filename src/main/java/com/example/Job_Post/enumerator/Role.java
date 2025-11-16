package com.example.Job_Post.enumerator;

public enum Role {
    USER,
    ADMIN;

    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }
}
