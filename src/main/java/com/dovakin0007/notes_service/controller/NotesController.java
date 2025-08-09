package com.dovakin0007.notes_service.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotesController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        System.out.println("Hello endpoint called");
        return ResponseEntity.ok("Hello, World!");
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok("Logged in as: " + principal.getName() + principal.toString());
    }
        @GetMapping("/u")
    public ResponseEntity<String> u(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok("Logged in as: " + principal.getName());
    }
}
