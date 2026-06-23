package com.cherry.core.properties;

/**
 * @author cherry
 * @version 1.0.0
 * @ClassName SystemProperties
 * @Description
 * @createTime 2023年12月27日 11:23:00
 */
@SuppressWarnings("all")
public class SystemProperties {

    public static void init() {
        System.setProperty("pagehelper.banner", "false");
        System.setProperty("netty.server.port", "9788");
    }

}
