package com.example.Job_Post.enumerator;

public enum Status {
    ONLINE,
    OFFLINE;

    
    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }

}
