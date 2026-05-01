package com.analyzer.analyzer.user.auth;

import com.analyzer.analyzer.advice.exceptions.BadRequestException;
import com.analyzer.analyzer.security.jwt.JwtAuthResponse;
import com.analyzer.analyzer.security.jwt.JwtTokenProvider;
import com.analyzer.analyzer.user.User;
import com.analyzer.analyzer.user.UserRepository;
import com.analyzer.analyzer.user.auth.dtos.LoginDto;
import com.analyzer.analyzer.user.auth.dtos.RegisterDto;
import com.analyzer.analyzer.user.mapper.RegisterMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return jwtAuthResponse;
    }

    public String register(RegisterDto registerDto) throws BadRequestException {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BadRequestException(
                    "The user with the following username already exists.");
        }

        User user = RegisterMapper.map(registerDto, passwordEncoder);

        userRepository.save(user);
        return "User registered successfully";
    }
}

