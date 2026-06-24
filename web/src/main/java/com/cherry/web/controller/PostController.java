package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.base.utils.IpUtil;
import com.cherry.model.param.post.PostPageParam;
import com.cherry.model.base.page.CherryCommonPage;
import com.cherry.model.param.post.PublishPostParam;
import com.cherry.model.vo.post.PostVO;
import com.cherry.service.api.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/post")
@RequiredArgsConstructor
@Tag(name = "Post Management Interface")
public class PostController {

    private final PostService postService;

    @AllowAnonymousAccess
    @PostMapping("/publish")
    @Operation(summary = "Publish Post")
    public CherryResponseEntity<Void> publishPost(@RequestBody PublishPostParam param, HttpServletRequest request) {
        postService.publishPost(param, IpUtil.getIpAddr(request));
        return CherryResponseEntity.success();
    }

    @AllowAnonymousAccess
    @PostMapping("/page")
    @Operation(summary = "Get Post Page")
    public CherryResponseEntity<CherryCommonPage<PostVO>> getPostPage(
            @RequestBody PostPageParam param, HttpServletRequest request
    ) {
        CherryCommonPage<PostVO> page = postService.getPostPage(param, IpUtil.getIpAddr(request));
        return CherryResponseEntity.success(page);
    }

    @AllowAnonymousAccess
    @GetMapping("/{id}")
    @Operation(summary = "Get Post Detail")
    public CherryResponseEntity<PostVO> getPostDetail(@PathVariable Long id, HttpServletRequest request) {
        PostVO detail = postService.getPostDetail(id, IpUtil.getIpAddr(request));
        return CherryResponseEntity.success(detail);
    }
}
