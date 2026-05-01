package com.analyzer.analyzer.user.auth;

import com.analyzer.analyzer.security.jwt.JwtAuthResponse;
import com.analyzer.analyzer.user.auth.dtos.LoginDto;
import com.analyzer.analyzer.user.auth.dtos.RegisterDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public JwtAuthResponse login(@Valid @RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }
}
