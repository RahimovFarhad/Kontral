package com.example.Job_Post.enumerator;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PreferredRole {
    EMPLOYER("EMPLOYER"),
    EMPLOYEE("EMPLOYEE"),
    ALL("All");

    private final String apiValue;

    PreferredRole(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static PreferredRole fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("preferredRole is required");
        }

        String normalized = value.trim();
        if ("All".equalsIgnoreCase(normalized) || "ALL".equalsIgnoreCase(normalized)) {
            return ALL;
        }

        return Arrays.stream(values())
            .filter(role -> role.name().equalsIgnoreCase(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Invalid preferredRole. Allowed values: EMPLOYER, EMPLOYEE, All"
            ));
    }
}
