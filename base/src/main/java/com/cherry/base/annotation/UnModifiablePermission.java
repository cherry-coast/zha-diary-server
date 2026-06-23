package com.cherry.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年12月19日 16:45:00
 * ClassName UnModifiablePermission
 * packageName com.cherry.animal.base.annotation
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface UnModifiablePermission {

    /**
     * phone number
     *
     * @return phone number
     */
    String permissionId() default "0";

}
