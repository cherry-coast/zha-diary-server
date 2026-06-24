package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.param.post.PostPageParam;
import com.cherry.model.base.page.CherryCommonPage;
import com.cherry.model.entity.Post;
import com.cherry.model.param.post.PublishPostParam;
import com.cherry.model.vo.post.PostVO;

public interface PostService extends IService<Post> {
    void publishPost(PublishPostParam param, String ip);

    CherryCommonPage<PostVO> getPostPage(PostPageParam param, String ip);

    PostVO getPostDetail(Long id, String ip);
}
