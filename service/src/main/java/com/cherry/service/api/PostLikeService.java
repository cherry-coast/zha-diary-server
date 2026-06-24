package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.entity.PostLike;
import com.cherry.model.param.post.LikeParam;

public interface PostLikeService extends IService<PostLike> {
    void toggleLike(LikeParam param, String ip);
}
