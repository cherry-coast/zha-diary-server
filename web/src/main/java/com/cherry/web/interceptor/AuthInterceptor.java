package com.cherry.web.interceptor;

import com.cherry.base.annotation.AllowAnonymousAccess;
import com.cherry.base.domain.threadlocal.UserContext;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查是否允许匿名访问
        if (handlerMethod.hasMethodAnnotation(AllowAnonymousAccess.class) || 
            handlerMethod.getBeanType().isAnnotationPresent(AllowAnonymousAccess.class)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            throw new CherryException(BaseExceptionEnum.NO_AUTHORIZE.getErrorCode(), "未登录，请先登录");
        }
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            UserContext.User user = TokenUtil.parseToken(token);
            UserContext.setUser(user);
            
            String requestURI = request.getRequestURI();
            
            // 后台接口，校验 userType == 2
            if (requestURI.startsWith("/v1/admin/")) {
                if (user.getUserType() == null || user.getUserType() != 2) {
                    throw new CherryException(403, "没有后台访问权限");
                }
            } else {
                // 前台接口，校验 userType == 1 (可选，这里严格校验)
                if (user.getUserType() == null || user.getUserType() != 1) {
                    throw new CherryException(403, "没有前台访问权限");
                }
            }
            
        } catch (CherryException e) {
            throw e;
        } catch (Exception e) {
            throw new CherryException(BaseExceptionEnum.NO_AUTHORIZE.getErrorCode(), "Token无效或已过期");
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}
