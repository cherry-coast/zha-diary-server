package com.cherry.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.entity.AdminUser;
import com.cherry.model.param.admin.AdminLoginParam;
import com.cherry.model.vo.auth.AuthVO;

public interface AdminUserService extends IService<AdminUser> {

    /**
     * Admin login
     *
     * @param param login info
     * @return AuthVO with token
     */
    AuthVO login(AdminLoginParam param);

}
