package com.cherry.base.annotation;

import java.lang.annotation.*;

/**
 * 允许匿名访问
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AllowAnonymousAccess {
}
