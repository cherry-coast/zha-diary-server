package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.base.utils.IpUtil;
import com.cherry.model.param.post.LikeParam;
import com.cherry.service.api.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/like")
@RequiredArgsConstructor
@Tag(name = "Post Like Interface")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @AllowAnonymousAccess
    @PostMapping("/toggle")
    @Operation(summary = "Toggle Like")
    public CherryResponseEntity<Void> toggleLike(@RequestBody LikeParam param, HttpServletRequest request) {
        postLikeService.toggleLike(param, IpUtil.getIpAddr(request));
        return CherryResponseEntity.success();
    }
}
