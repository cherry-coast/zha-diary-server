package com.cherry.web.controller;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.model.vo.post.PostCategoryVO;
import com.cherry.service.api.PostCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/post-category")
@RequiredArgsConstructor
@Tag(name = "Post Category Interface")
public class PostCategoryController {

    private final PostCategoryService postCategoryService;

    @AllowAnonymousAccess
    @GetMapping("/list")
    @Operation(summary = "Get Category List")
    public CherryResponseEntity<List<PostCategoryVO>> getCategoryList() {
        List<PostCategoryVO> list = postCategoryService.getCategoryList();
        return CherryResponseEntity.success(list);
    }
}
