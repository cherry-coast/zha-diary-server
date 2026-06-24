package com.cherry.base.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.cherry.base.domain.threadlocal.UserContext;

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

    public static String generateToken(Long userId, String username, Integer userType) {
        Map<String, Object> data = new HashMap<>() {{
            put("username", username);
            put("userType", userType);
        }};
        Calendar instance=Calendar.getInstance();
        instance.add(Calendar.HOUR,24);
        //获取token
        return JWT.create()
                .withHeader(data)
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("userType", userType)
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SIGN));
    }

    public static UserContext.User parseToken(String token) {
        //创建验证对象
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SIGN)).build();
        //验证token
        DecodedJWT verify = jwtVerifier.verify(token);
        
        Long userId = verify.getClaim("userId").asLong();
        String username = verify.getClaim("username").asString();
        Integer userType = verify.getClaim("userType").asInt();
        
        return UserContext.User.builder()
                .id(userId)
                .name(username)
                .userType(userType)
                .build();
    }

}
