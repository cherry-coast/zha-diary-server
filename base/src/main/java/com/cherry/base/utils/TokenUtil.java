package com.cherry.base.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.cherry.base.domain.common.CommonModel2;

import java.util.*;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年10月12日 16:12:00
 * ClassName TokenUtil
 * packageName com.cherry.animal.base.utils
 */
@SuppressWarnings("unused")
public class TokenUtil {

    public static final List<String> BLACK_LIST = new ArrayList<>();

    private static final String SIGN = "cf268818291a";

    public static String generateToken(Long userId, String username) {
        Map<String, Object> data = new HashMap<>() {{
            put("username", username);
        }};
        Calendar instance=Calendar.getInstance();
        instance.add(Calendar.HOUR,24);
        //获取token
        return JWT.create()
                .withHeader(data)
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SIGN));
    }

    public static CommonModel2<Long, Long> parseToken(String token) {
        CommonModel2<Long, Long> commonModel = new CommonModel2<>();
        //创建验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGN)).build();
        //验证token
        DecodedJWT verify = jwtVerifier.verify(token);
        commonModel.setT1(verify.getExpiresAt().getTime());
        commonModel.setT2(verify.getClaim("userId").asLong());
        return commonModel;
    }

}
