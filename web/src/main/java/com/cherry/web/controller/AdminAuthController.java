package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.model.param.admin.AdminLoginParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Admin Authentication Interface")
public class AdminAuthController {

    private final AdminUserService adminUserService;

    @AllowAnonymousAccess
    @PostMapping("/login")
    @Operation(summary = "Admin Login")
    public CherryResponseEntity<AuthVO> login(@RequestBody AdminLoginParam param) {
        AuthVO authVO = adminUserService.login(param);
        return CherryResponseEntity.success(authVO);
    }

}
