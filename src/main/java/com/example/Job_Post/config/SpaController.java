package com.example.Job_Post.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
        "/",
        "/home",
        "/browse/**",
        "/signup",
        "/post",
        "/profile/**",
        "/chat/**",
        "/notifications",
        "/jobs/**",
        "/mygigs/**",
        "/forgot-password",
        "/reset-password"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
