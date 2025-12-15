package com.example.Job_Post.component;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.UserRepository;

@Component
@RequestScope
public class CurrentUser {

    private final User user;

    public CurrentUser(UserRepository userRepository) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        this.user = userRepository.findByEmail(email)
                .orElse(null);
    }

    public User get() {
        return user;
    }
}
