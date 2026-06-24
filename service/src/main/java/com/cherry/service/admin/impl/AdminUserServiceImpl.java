package com.cherry.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryAesUtil;
import com.cherry.base.utils.TokenUtil;
import com.cherry.database.mapper.AdminUserMapper;
import com.cherry.model.entity.AdminUser;
import com.cherry.model.param.admin.AdminLoginParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final AdminUserMapper adminUserMapper;

    @Override
    public AuthVO login(AdminLoginParam param) {
        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminUser::getUsername, param.getUsername());
        AdminUser adminUser = adminUserMapper.selectOne(queryWrapper);

        if (adminUser == null) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名或密码错误");
        }
        
        if (adminUser.getStatus() != null && adminUser.getStatus() == 0) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "该账号已被禁用");
        }

        String decryptedPassword = CherryAesUtil.decrypt(adminUser.getPassword());
        if (!decryptedPassword.equals(param.getPassword())) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "用户名或密码错误");
        }

        // userType = 2 for Admin
        String token = TokenUtil.generateToken(adminUser.getId(), adminUser.getUsername(), 2);
        
        AuthVO authVO = new AuthVO();
        authVO.setToken(token);
        authVO.setUserId(adminUser.getId());
        authVO.setUsername(adminUser.getUsername());
        return authVO;
    }
}
