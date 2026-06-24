package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.model.param.post.PublishCommentParam;
import com.cherry.model.vo.post.CommentVO;
import com.cherry.service.api.PostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/comment")
@RequiredArgsConstructor
@Tag(name = "Post Comment Interface")
public class PostCommentController {

    private final PostCommentService postCommentService;

    // Comments require login, no @AllowAnonymousAccess here
    @PostMapping("/publish")
    @Operation(summary = "Publish Comment")
    public CherryResponseEntity<Void> publishComment(@RequestBody PublishCommentParam param) {
        postCommentService.publishComment(param);
        return CherryResponseEntity.success();
    }

    @AllowAnonymousAccess
    @GetMapping("/list")
    @Operation(summary = "Get Comment List for Post")
    public CherryResponseEntity<List<CommentVO>> getCommentList(@RequestParam("postId") Long postId) {
        List<CommentVO> comments = postCommentService.getCommentTree(postId);
        return CherryResponseEntity.success(comments);
    }
}
