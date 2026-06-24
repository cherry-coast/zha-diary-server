package com.cherry.model.enums;

import lombok.Getter;

/**
 * @author cherry
 * @version 1.0.0
 * Description: 验证码类型枚举
 * Date: 2026年06月24日
 * ClassName: CodeTypeEnum
 * packageName: com.cherry.model.enums
 */
@Getter
public enum CodeTypeEnum {
    
    /**
     * 注册
     */
    REGISTER(1, "注册"),
    
    /**
     * 登录
     */
    LOGIN(2, "登录"),
    
    /**
     * 修改信息
     */
    UPDATE_INFO(3, "修改信息");

    private final Integer code;
    private final String description;

    CodeTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CodeTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CodeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
