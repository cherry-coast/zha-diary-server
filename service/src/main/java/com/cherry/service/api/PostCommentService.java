package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.entity.PostComment;
import com.cherry.model.param.post.PublishCommentParam;
import com.cherry.model.vo.post.CommentVO;

import java.util.List;

public interface PostCommentService extends IService<PostComment> {
    void publishComment(PublishCommentParam param);

    List<CommentVO> getCommentTree(Long postId);
}
