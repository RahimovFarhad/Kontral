package com.example.Job_Post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Job_Post.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            return ResponseEntity.ok(dashboardService.getSummary());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get dashboard summary: " + e.getMessage());
        }
    }
}
