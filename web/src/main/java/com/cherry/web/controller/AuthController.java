package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.model.param.auth.LoginParam;
import com.cherry.model.param.auth.RegisterParam;
import com.cherry.model.param.auth.SendCodeParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Interface")
public class AuthController {

    private final UserService userService;

    @AllowAnonymousAccess
    @PostMapping("/register")
    @Operation(summary = "User Registration")
    public CherryResponseEntity<AuthVO> register(@RequestBody RegisterParam param) {
        AuthVO authVO = userService.register(param);
        return CherryResponseEntity.success(authVO);
    }

    @AllowAnonymousAccess
    @PostMapping("/login")
    @Operation(summary = "User Login")
    public CherryResponseEntity<AuthVO> login(@RequestBody LoginParam param) {
        AuthVO authVO = userService.login(param);
        return CherryResponseEntity.success(authVO);
    }

    @AllowAnonymousAccess
    @PostMapping("/send-code")
    @Operation(summary = "Send Email Verification Code")
    public CherryResponseEntity<String> sendCode(@RequestBody SendCodeParam param) {
        String code = userService.sendCode(param.getEmail(), param.getType());
        return CherryResponseEntity.success(code);
    }

}
