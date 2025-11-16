package com.example.Job_Post.enumerator;

public enum AuthMethod {
    Custom,
    OAuth2;

    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }
}
