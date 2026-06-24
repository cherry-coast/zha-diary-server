package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.domain.threadlocal.UserContext;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.database.mapper.PostCategoryMapper;
import com.cherry.database.mapper.PostLikeMapper;
import com.cherry.database.mapper.PostMapper;
import com.cherry.database.mapper.UserMapper;
import com.cherry.model.base.page.CherryCommonPage;
import com.cherry.model.entity.Post;
import com.cherry.model.entity.PostCategory;
import com.cherry.model.entity.PostLike;
import com.cherry.model.entity.User;
import com.cherry.model.param.post.PostPageParam;
import com.cherry.model.param.post.PublishPostParam;
import com.cherry.model.vo.post.PostVO;
import com.cherry.service.api.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostCategoryMapper postCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishPost(PublishPostParam param, String ip) {
        Post post = new Post();
        post.setContent(param.getContent());
        post.setAppearance(param.getAppearance());
        post.setLikeCount(0);
        post.setCommentCount(0);

        UserContext.User user = UserContext.getUser();
        if (user != null && user.getId() != null) {
            post.setUserId(user.getId());
        } else {
            post.setIpAddress(ip);
        }

        if (param.getCategoryNames() != null && !param.getCategoryNames().isEmpty()) {
            List<String> validCategories = param.getCategoryNames().stream()
                    .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            if (!validCategories.isEmpty()) {
                LambdaQueryWrapper<PostCategory> catQuery = new LambdaQueryWrapper<>();
                catQuery.in(PostCategory::getName, validCategories);
                List<String> existingCategories = postCategoryMapper.selectList(catQuery)
                        .stream().map(PostCategory::getName).toList();

                for (String categoryName : validCategories) {
                    if (!existingCategories.contains(categoryName)) {
                        PostCategory newCat = new PostCategory();
                        newCat.setName(categoryName);
                        newCat.setType(2);
                        newCat.setSort(99);
                        postCategoryMapper.insert(newCat);
                    }
                }
            }
            post.setCategoryName(String.join(",", param.getCategoryNames()));
        }

        this.save(post);
    }

    @Override
    public CherryCommonPage<PostVO> getPostPage(PostPageParam param, String ip) {
        Page<Post> page = new Page<>(param.getPageNo(), param.getPageSize());
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getCategoryName()) && !"全部".equals(param.getCategoryName())) {
            queryWrapper.apply("FIND_IN_SET({0}, category_name)", param.getCategoryName());
        }
        queryWrapper.orderByDesc(Post::getInsertTime);

        Page<Post> postPage = this.page(page, queryWrapper);
        List<Post> posts = postPage.getRecords();

        if (posts.isEmpty()) {
            return CherryCommonPage.transferPageData(
                    CherryCommonPage.restPage(postPage), new ArrayList<>(), PostVO.class
            );
        }

        // Get User IDs to fetch avatars and usernames
        List<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> userMap = null;
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        UserContext.User user = UserContext.getUser();
        boolean isLogged = user != null && user.getId() != null;

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        LambdaQueryWrapper<PostLike> likeQuery = new LambdaQueryWrapper<>();
        likeQuery.in(PostLike::getPostId, postIds);
        if (isLogged) {
            likeQuery.eq(PostLike::getUserId, user.getId());
        } else {
            likeQuery.isNull(PostLike::getUserId);
            likeQuery.eq(PostLike::getIpAddress, ip);
        }
        
        List<PostLike> userLikes = postLikeMapper.selectList(likeQuery);
        List<Long> likedPostIds = userLikes.stream().map(PostLike::getPostId).toList();

        List<PostVO> voList = new ArrayList<>();
        for (Post post : posts) {
            PostVO vo = new PostVO();
            BeanUtils.copyProperties(post, vo);

            if (post.getUserId() != null && userMap != null && userMap.containsKey(post.getUserId())) {
                User u = userMap.get(post.getUserId());
                vo.setUsername(u.getUsername());
                vo.setAvatar(u.getAvatar());
            } else {
                vo.setUsername("匿名吃瓜群众");
                vo.setAvatar("https://api.dicebear.com/7.x/bottts/svg?seed=melon");
            }

            if (StringUtils.isNotBlank(post.getCategoryName())) {
                vo.setCategoryNames(Arrays.asList(post.getCategoryName().split(",")));
            }
            vo.setIsLiked(likedPostIds.contains(post.getId()));
            voList.add(vo);
        }

        return CherryCommonPage.transferPageData(CherryCommonPage.restPage(postPage), voList, PostVO.class);
    }

    @Override
    public PostVO getPostDetail(Long id, String ip) {
        Post post = this.getById(id);
        if (post == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "瓜不存在");
        }

        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);

        if (post.getUserId() != null) {
            User u = userMapper.selectById(post.getUserId());
            if (u != null) {
                vo.setUsername(u.getUsername());
                vo.setAvatar(u.getAvatar());
            }
        } else {
            vo.setUsername("匿名吃瓜群众");
            vo.setAvatar("https://api.dicebear.com/7.x/bottts/svg?seed=melon");
        }

        UserContext.User user = UserContext.getUser();
        boolean isLogged = user != null && user.getId() != null;
        
        LambdaQueryWrapper<PostLike> likeQuery = new LambdaQueryWrapper<>();
        likeQuery.eq(PostLike::getPostId, id);
        if (isLogged) {
            likeQuery.eq(PostLike::getUserId, user.getId());
        } else {
            likeQuery.isNull(PostLike::getUserId);
            likeQuery.eq(PostLike::getIpAddress, ip);
        }
        if (StringUtils.isNotBlank(post.getCategoryName())) {
            vo.setCategoryNames(Arrays.asList(post.getCategoryName().split(",")));
        }
        vo.setIsLiked(postLikeMapper.exists(likeQuery));

        return vo;
    }
}
