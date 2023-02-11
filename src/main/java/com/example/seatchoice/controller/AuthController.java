package com.example.seatchoice.controller;

import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.dto.auth.UserInfo;
import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.param.AuthParam;
import com.example.seatchoice.service.AuthService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<Token> login(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody AuthParam authParam
    ) {
        Token login = authService.login(request, response, authParam);
        return new ApiResponse<>(login);
    }

    @GetMapping("/refresh")
    public ApiResponse<Token> refreshToken (HttpServletRequest request, HttpServletResponse response) {
        Token token = authService.refreshToken(request, response);
        return new ApiResponse<>(token);
    }
}
