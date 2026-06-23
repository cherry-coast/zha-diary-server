package com.cherry.base.annotation;

import java.lang.annotation.*;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年10月14日 16:19:00
 * ClassName AdminPermission
 * packageName com.cherry.animal.base.annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AdminPermission {
}
