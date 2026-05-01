package com.analyzer.analyzer.user.auth;

import com.analyzer.analyzer.security.jwt.JwtAuthResponse;
import com.analyzer.analyzer.user.auth.dtos.LoginDto;
import com.analyzer.analyzer.user.auth.dtos.RegisterDto;

public interface AuthService {
    JwtAuthResponse login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}

