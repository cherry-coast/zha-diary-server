package com.cherry.base.domain.threadlocal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年10月14日 15:41:00
 * ClassName UserContext
 * packageName com.cherry.animal.base.domain.threadlocal
 */
public class UserContext {

    private static final ThreadLocal<User> USER = new InheritableThreadLocal<>();

    public static void setUser(User user) {
        USER.set(user);
    }

    public static User getUser() {
        return USER.get();
    }

    public static void clear() {
        USER.remove();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        private Long id;

        private String name;

        private Integer userType;

    }
}
