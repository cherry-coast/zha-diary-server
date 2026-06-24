package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.param.auth.LoginParam;
import com.cherry.model.param.auth.RegisterParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.model.entity.User;

public interface UserService extends IService<User> {

    /**
     * User registration
     *
     * @param registerReq Registration details
     * @return AuthRes with user info
     */
    AuthVO register(RegisterParam param);

    /**
     * User login
     *
     * @param loginReq Login credentials
     * @return AuthRes with token
     */
    AuthVO login(LoginParam param);

}
