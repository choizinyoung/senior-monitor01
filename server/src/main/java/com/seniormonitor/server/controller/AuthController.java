package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.ApiResponse;
import com.seniormonitor.server.dto.LoginRequest;
import com.seniormonitor.server.dto.LoginResponse;
import com.seniormonitor.server.dto.ManagerResponse;
import com.seniormonitor.server.dto.SignupRequest;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // API: 회원가입 신청 (담당자)
    @PostMapping("/api/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ManagerResponse> signup(@RequestBody SignupRequest req) {
        return ApiResponse.ok(authService.signup(req));
    }

    // API: 로그인
    @PostMapping("/api/auth/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req));
    }

    // API: 로그아웃 (현재 토큰 무효화)
    @PostMapping("/api/auth/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CurrentManager currentManager) {
        authService.logout(currentManager);
        return ApiResponse.ok(null);
    }
}
