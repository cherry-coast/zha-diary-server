package com.cherry.service.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryAesUtil;
import com.cherry.base.utils.CherryMailUtil;
import com.cherry.base.utils.CherryOssUtil;
import com.cherry.base.utils.TokenUtil;
import com.cherry.database.mapper.UserMapper;
import com.cherry.model.entity.User;
import com.cherry.model.param.auth.LoginParam;
import com.cherry.model.param.auth.RegisterParam;
import com.cherry.model.param.user.UpdateUserInfoParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.service.api.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String CACHE_KEY_AUTH_CODE = "auth:code:";

    private final Cache<String, String> codeCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final UserMapper userMapper;
    private final CherryOssUtil cherryOssUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthVO register(RegisterParam param) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, param.getUsername());
        if (userMapper.exists(queryWrapper)) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名已存在");
        }

        String cacheKey = CACHE_KEY_AUTH_CODE + "1:" + param.getEmail();
        String cachedCode = codeCache.getIfPresent(cacheKey);
        if (cachedCode == null || !cachedCode.equals(param.getCode())) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "验证码错误或已过期");
        }

        User user = new User();
        user.setUsername(param.getUsername());
        user.setPassword(CherryAesUtil.encrypt(param.getPassword()));
        user.setEmail(param.getEmail());

        userMapper.insert(user);

        codeCache.invalidate(cacheKey);

        return buildAuthRes(user);
    }

    @Override
    public AuthVO login(LoginParam param) {
        if (param.getLoginType() == null || param.getLoginType() == 1) {
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
        } else if (param.getLoginType() == 2) {
            String cacheKey = CACHE_KEY_AUTH_CODE + "2:" + param.getEmail();
            String cachedCode = codeCache.getIfPresent(cacheKey);
            if (cachedCode == null || !cachedCode.equals(param.getCode())) {
                throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "验证码错误或已过期");
            }

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, param.getEmail());
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "该邮箱未注册");
            }

            codeCache.invalidate(cacheKey);
            return buildAuthRes(user);
        } else {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "不支持的登录类型");
        }
    }

    private AuthVO buildAuthRes(User user) {
        String token = TokenUtil.generateToken(user.getId(), user.getUsername(), 1);
        AuthVO authVO = new AuthVO();
        authVO.setToken(token);
        authVO.setUserId(user.getId());
        authVO.setUsername(user.getUsername());
        authVO.setAvatar(user.getAvatar());
        return authVO;
    }

    @Override
    public String sendCode(String email, Integer type) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        boolean exists = userMapper.exists(queryWrapper);

        if (type == 1 && exists) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "该邮箱已经注册");
        }
        if ((type == 2 || type == 3) && !exists) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "该邮箱未注册");
        }

        String code = RandomStringUtils.randomNumeric(6);
        String cacheKey = CACHE_KEY_AUTH_CODE + type + ":" + email;
        codeCache.put(cacheKey, code);

        try {
            CherryMailUtil.postMessage("注册验证码", "您的验证码是：" + code + "，5分钟内有效。", Collections.singletonList(email));
        } catch (Exception e) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "发送验证码失败");
        }
        
        return code;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        return cherryOssUtil.uploadImage(file);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UpdateUserInfoParam param, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户不存在");
        }

        if (StringUtils.isNotBlank(param.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, param.getUsername());
            queryWrapper.ne(User::getId, userId);
            if (userMapper.exists(queryWrapper)) {
                throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名已存在");
            }
            user.setUsername(param.getUsername());
        }

        if (StringUtils.isNotBlank(param.getAvatar())) {
            user.setAvatar(param.getAvatar());
        }

        if (StringUtils.isNotBlank(param.getPassword())) {
            if (StringUtils.isBlank(param.getEmail()) || StringUtils.isBlank(param.getCode())) {
                throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "修改密码需提供邮箱和验证码");
            }
            
            // Validate code
            String cacheKey = CACHE_KEY_AUTH_CODE + "3:" + param.getEmail();
            String cachedCode = codeCache.getIfPresent(cacheKey);
            if (cachedCode == null || !cachedCode.equals(param.getCode())) {
                throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "验证码错误或已过期");
            }
            
            user.setPassword(CherryAesUtil.encrypt(param.getPassword()));
            codeCache.invalidate(cacheKey);
        }

        userMapper.updateById(user);
    }
}
