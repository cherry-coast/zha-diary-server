package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.domain.threadlocal.UserContext;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.database.mapper.PostLikeMapper;
import com.cherry.database.mapper.PostMapper;
import com.cherry.model.entity.Post;
import com.cherry.model.entity.PostLike;
import com.cherry.model.param.post.LikeParam;
import com.cherry.service.api.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(LikeParam param, String ip) {
        if (param.getTargetType() != 1) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "目前只支持给瓜点赞");
        }

        Post post = postMapper.selectById(param.getTargetId());
        if (post == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "目标不存在");
        }

        UserContext.User user = UserContext.getUser();
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, param.getTargetId());

        boolean isLogged = user != null && user.getId() != null;

        if (isLogged) {
            queryWrapper.eq(PostLike::getUserId, user.getId());
        } else {
            queryWrapper.isNull(PostLike::getUserId);
            queryWrapper.eq(PostLike::getIpAddress, ip);
        }

        PostLike existingLike = this.getOne(queryWrapper);

        if (existingLike != null) {
            // 取消点赞
            this.removeById(existingLike.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            // 新增点赞
            PostLike newLike = new PostLike();
            newLike.setPostId(param.getTargetId());
            if (isLogged) {
                newLike.setUserId(user.getId());
            } else {
                newLike.setIpAddress(ip);
            }
            this.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        
        postMapper.updateById(post);
    }
}
