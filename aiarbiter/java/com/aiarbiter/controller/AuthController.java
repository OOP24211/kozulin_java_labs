package com.aiarbiter.controller;

import com.aiarbiter.model.User;
import com.aiarbiter.model.auth.RegisterRequest;
import com.aiarbiter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Map.of("username", user.getUsername());
    }

    @GetMapping("/me")
    public Map<String, String> me(@AuthenticationPrincipal User user) {
        return Map.of("username", user.getUsername());
    }

}
