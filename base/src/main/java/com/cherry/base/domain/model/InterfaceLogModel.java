package com.cherry.base.domain.model;

import lombok.*;

/**
 * @author : cherry
 * Date: 2022/04/21 15:40
 * Description:
 * ClassName: InterfaceLog
 * Package: com.cherry.common.domain
 * Copyright (c) 2022,All Rights Reserved.
 */
@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceLogModel {

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作用户
     */
    private String username;

    /**
     * 消耗时间
     */
    private Long spendTime;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * URI
     */
    private String uri;

    /**
     * URL
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 返回结果
     */
    private Object result;

    /**
     * 开始时间
     */
    private Long recodeTime;

}