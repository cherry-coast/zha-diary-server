package com.cherry.web.controller;

import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.base.domain.threadlocal.UserContext;
import com.cherry.service.api.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management Interface")
public class UserController {

    private final UserService userService;

    @PostMapping("/avatar")
    @Operation(summary = "Upload User Avatar")
    public CherryResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return CherryResponseEntity.success(userService.uploadAvatar(file));
    }

    @PostMapping("/update")
    @Operation(summary = "Update User Info")
    public CherryResponseEntity<Void> updateUserInfo(@org.springframework.web.bind.annotation.RequestBody com.cherry.model.param.user.UpdateUserInfoParam param) {
        UserContext.User user = UserContext.getUser();
        userService.updateUserInfo(param, user.getId());
        return CherryResponseEntity.success();
    }

}
