package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.database.mapper.PostCategoryMapper;
import com.cherry.database.mapper.PostMapper;
import com.cherry.model.entity.Post;
import com.cherry.model.entity.PostCategory;
import com.cherry.model.vo.post.PostCategoryVO;
import com.cherry.service.api.PostCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostCategoryServiceImpl extends ServiceImpl<PostCategoryMapper, PostCategory> implements PostCategoryService {

    private final PostMapper postMapper;

    @Override
    public List<PostCategoryVO> getCategoryList() {
        // Query all categories, order by sort asc
        LambdaQueryWrapper<PostCategory> categoryQuery = new LambdaQueryWrapper<>();
        categoryQuery.orderByAsc(PostCategory::getSort);
        List<PostCategory> categories = this.list(categoryQuery);

        // Calculate count by category name
        // Since we don't have too many posts, doing a group by or fetching and grouping in memory is fine.
        // We'll use memory grouping for simplicity since we want counts for each name.
        LambdaQueryWrapper<Post> postQuery = new LambdaQueryWrapper<>();
        postQuery.isNotNull(Post::getCategoryName);
        postQuery.select(Post::getCategoryName); // only select what's needed
        List<Post> posts = postMapper.selectList(postQuery);

        Map<String, Long> countMap = new java.util.HashMap<>();
        for (Post post : posts) {
            if (StringUtils.isNotBlank(post.getCategoryName())) {
                String[] cats = post.getCategoryName().split(",");
                for (String cat : cats) {
                    countMap.put(cat, countMap.getOrDefault(cat, 0L) + 1);
                }
            }
        }

        List<PostCategoryVO> voList = new ArrayList<>();
        
        // Ensure "全部" is placed at the top or handled by frontend. The UI has "全部".
        // The requirements say returning all categories and their counts.
        for (PostCategory category : categories) {
            PostCategoryVO vo = new PostCategoryVO();
            BeanUtils.copyProperties(category, vo);
            long count = countMap.getOrDefault(category.getName(), 0L);
            vo.setPostCount((int) count);
            voList.add(vo);
        }

        return voList;
    }
}
