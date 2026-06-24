package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.domain.threadlocal.UserContext;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.database.mapper.PostCommentMapper;
import com.cherry.database.mapper.PostMapper;
import com.cherry.database.mapper.UserMapper;
import com.cherry.model.entity.Post;
import com.cherry.model.entity.PostComment;
import com.cherry.model.entity.User;
import com.cherry.model.param.post.PublishCommentParam;
import com.cherry.model.vo.post.CommentVO;
import com.cherry.service.api.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements PostCommentService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishComment(PublishCommentParam param) {
        UserContext.User user = UserContext.getUser();
        if (user == null || user.getId() == null) {
            throw new CherryException(BaseExceptionEnum.NO_AUTHORIZE.getErrorCode(), "请先登录");
        }

        Post post = postMapper.selectById(param.getPostId());
        if (post == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "瓜不存在");
        }

        PostComment comment = new PostComment();
        comment.setPostId(param.getPostId());
        comment.setUserId(user.getId());
        comment.setContent(param.getContent());
        comment.setParentId(param.getParentId());
        comment.setReplyToUserId(param.getReplyToUserId());
        comment.setLikeCount(0);
        
        this.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postMapper.updateById(post);
    }

    @Override
    public List<CommentVO> getCommentTree(Long postId) {
        LambdaQueryWrapper<PostComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostComment::getPostId, postId);
        queryWrapper.orderByAsc(PostComment::getInsertTime);
        List<PostComment> comments = this.list(queryWrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有涉及的用户
        List<Long> userIds = comments.stream().map(PostComment::getUserId).collect(Collectors.toList());
        List<Long> replyUserIds = comments.stream().map(PostComment::getReplyToUserId).filter(Objects::nonNull).toList();
        userIds.addAll(replyUserIds);
        userIds = userIds.stream().distinct().collect(Collectors.toList());

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<CommentVO> voList = new ArrayList<>();
        for (PostComment comment : comments) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            
            User u = userMap.get(comment.getUserId());
            if (u != null) {
                vo.setUsername(u.getUsername());
                vo.setAvatar(u.getAvatar());
            }

            if (comment.getReplyToUserId() != null) {
                User replyU = userMap.get(comment.getReplyToUserId());
                if (replyU != null) {
                    vo.setReplyToUsername(replyU.getUsername());
                }
            }
            voList.add(vo);
        }

        // 组装树形结构
        List<CommentVO> rootComments = new ArrayList<>();
        Map<Long, List<CommentVO>> childrenMap = voList.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(CommentVO::getParentId));

        for (CommentVO vo : voList) {
            if (vo.getParentId() == null) {
                vo.setChildren(childrenMap.getOrDefault(vo.getId(), new ArrayList<>()));
                rootComments.add(vo);
            } else {
                // 如果是平铺的两层结构，需要把所有子回复挂在最顶层的评论下。这里简单处理为只要有 parentId 就在 childrenMap 里。
                // 这取决于前端需要树形还是平铺，如果前端只需要顶级和其子集，这里可以直接使用 childrenMap.
            }
        }

        return rootComments;
    }
}
