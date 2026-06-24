package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.entity.PostCategory;
import com.cherry.model.vo.post.PostCategoryVO;

import java.util.List;

public interface PostCategoryService extends IService<PostCategory> {
    List<PostCategoryVO> getCategoryList();
}
