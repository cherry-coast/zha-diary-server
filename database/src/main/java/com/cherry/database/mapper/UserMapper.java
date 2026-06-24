package com.cherry.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cherry.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
