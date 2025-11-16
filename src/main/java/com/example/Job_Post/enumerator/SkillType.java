package com.example.Job_Post.enumerator;

public enum SkillType {
    TECHNICAL,
    DESIGN,
    HANDYMAN,
    LANGUAGE;

    @Override
    public String toString() {
        return name().toLowerCase(); // or name() if you want uppercase strings
    }
}
