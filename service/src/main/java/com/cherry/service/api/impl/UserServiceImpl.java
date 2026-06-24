package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryAesUtil;
import com.cherry.base.utils.TokenUtil;
import com.cherry.database.mapper.UserMapper;
import com.cherry.model.entity.User;
import com.cherry.model.param.auth.LoginParam;
import com.cherry.model.param.auth.RegisterParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthVO register(RegisterParam param) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, param.getUsername());
        if (userMapper.exists(queryWrapper)) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名已存在");
        }

        User user = new User();
        user.setUsername(param.getUsername());
        user.setPassword(CherryAesUtil.encrypt(param.getPassword()));
        user.setEmail(param.getEmail());
        user.setNickname(param.getNickname());

        userMapper.insert(user);

        return buildAuthRes(user);
    }

    @Override
    public AuthVO login(LoginParam param) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, param.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名或密码错误");
        }

        String decryptedPassword = CherryAesUtil.decrypt(user.getPassword());
        if (!decryptedPassword.equals(param.getPassword())) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名或密码错误");
        }

        return buildAuthRes(user);
    }

    private AuthVO buildAuthRes(User user) {
        String token = TokenUtil.generateToken(user.getId(), user.getUsername());
        AuthVO authVO = new AuthVO();
        authVO.setToken(token);
        authVO.setUserId(user.getId());
        authVO.setUsername(user.getUsername());
        authVO.setNickname(user.getNickname());
        authVO.setAvatar(user.getAvatar());
        return authVO;
    }
}
