package com.cherry.service.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.model.param.auth.LoginParam;
import com.cherry.model.param.auth.RegisterParam;
import com.cherry.model.vo.auth.AuthVO;
import com.cherry.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {

    /**
     * User registration
     *
     * @param param Registration details
     * @return AuthRes with user info
     */
    AuthVO register(RegisterParam param);

    /**
     * User login
     *
     * @param param Login credentials
     * @return AuthRes with token
     */
    AuthVO login(LoginParam param);

    /**
     * Send email verification code
     *
     * @param email Email address
     * @param type  1 for register, 2 for login
     * @return The verification code
     */
    String sendCode(String email, Integer type);

    /**
     * upload user avatar
     *
     * @param file   avatar image file
     * @return avatar URL
     */
    String uploadAvatar(MultipartFile file);

}
