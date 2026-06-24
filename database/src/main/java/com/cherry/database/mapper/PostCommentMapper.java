package com.cherry.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.model.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
}
