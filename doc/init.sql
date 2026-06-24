CREATE DATABASE IF NOT EXISTS `zha_diary`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `zha_diary`;

CREATE TABLE `user`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    varchar(50)  NOT NULL COMMENT '用户名',
    `password`    varchar(255) NOT NULL COMMENT '密码(加密)',
    `salt`        varchar(255) DEFAULT NULL COMMENT '加密盐值',
    `avatar`      varchar(255) DEFAULT NULL COMMENT '头像',
    `email`       varchar(100) DEFAULT NULL COMMENT '邮箱',
    `insert_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`         tinyint(1)   DEFAULT '0' COMMENT '逻辑删除标记(0-未删除,1-已删除)',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `admin_user`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    varchar(50)  NOT NULL COMMENT '管理员账号',
    `password`    varchar(255) NOT NULL COMMENT '密码(加密)',
    `status`      tinyint(1)   DEFAULT '1' COMMENT '状态(0-禁用,1-启用)',
    `insert_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`         tinyint(1)   DEFAULT '0' COMMENT '逻辑删除',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='后台管理员表';

-- 1. 瓜（帖子）表
CREATE TABLE `post`
(
    `id`            bigint(20) NOT NULL COMMENT '主键ID',
    `user_id`       bigint(20)   DEFAULT NULL COMMENT '发布用户ID，NULL代表匿名',
    `content`       text COMMENT '富文本内容',
    `appearance`    tinyint(4)   DEFAULT NULL COMMENT '外观标识 1-4',
    `category_name` varchar(255) DEFAULT NULL COMMENT '分类名称，支持多选逗号分隔',
    `like_count`    int(11)      DEFAULT '0' COMMENT '点赞数',
    `comment_count` int(11)      DEFAULT '0' COMMENT '评论数',
    `ip_address`    varchar(50)  DEFAULT NULL COMMENT '匿名发布的IP地址',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`           tinyint(4)   DEFAULT '0' COMMENT '逻辑删除(0-正常, 1-删除)',
    `remark`        varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='瓜（帖子）表';

-- 2. 瓜的评论表
CREATE TABLE `post_comment`
(
    `id`               bigint(20) NOT NULL COMMENT '主键ID',
    `post_id`          bigint(20) NOT NULL COMMENT '所属瓜ID',
    `user_id`          bigint(20) NOT NULL COMMENT '评论用户ID（必须登录）',
    `parent_id`        bigint(20)   DEFAULT NULL COMMENT '父评论ID（用于嵌套回复）',
    `reply_to_user_id` bigint(20)   DEFAULT NULL COMMENT '回复的目标用户ID',
    `content`          text       NOT NULL COMMENT '评论内容',
    `like_count`       int(11)      DEFAULT '0' COMMENT '点赞数',
    `insert_time`      datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`              tinyint(4)   DEFAULT '0' COMMENT '逻辑删除(0-正常, 1-删除)',
    `remark`           varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='瓜的评论表';

-- 3. 瓜的点赞记录表
CREATE TABLE `post_like`
(
    `id`          bigint(20) NOT NULL COMMENT '主键ID',
    `post_id`     bigint(20) NOT NULL COMMENT '点赞的瓜ID',
    `user_id`     bigint(20)   DEFAULT NULL COMMENT '点赞用户ID（未登录为NULL）',
    `ip_address`  varchar(50)  DEFAULT NULL COMMENT '点赞IP地址（用于限制未登录用户点赞）',
    `insert_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`         tinyint(4)   DEFAULT '0' COMMENT '逻辑删除(0-正常, 1-删除)',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='瓜的点赞记录表';

-- 4. 瓜的分类表
CREATE TABLE `post_category`
(
    `id`          bigint(20)  NOT NULL COMMENT '主键ID',
    `name`        varchar(50) NOT NULL COMMENT '分类名称',
    `type`        tinyint(4)   DEFAULT '1' COMMENT '1-系统预设, 2-用户自定义',
    `sort`        int(11)      DEFAULT '99' COMMENT '排序号',
    `insert_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del`         tinyint(4)   DEFAULT '0' COMMENT '逻辑删除',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='瓜的分类字典表';

-- 5. 初始化预设分类数据
INSERT INTO `post_category` (`id`, `name`, `type`, `sort`)
VALUES (1, '办公', 1, 1),
       (2, '科技', 1, 2),
       (3, '旅行', 1, 3),
       (4, '美食', 1, 4),
       (5, '其他', 1, 5),
       (6, '情感', 1, 6),
       (7, '日常', 1, 7),
       (8, '娱乐', 1, 8);