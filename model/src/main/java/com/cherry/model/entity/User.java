package com.cherry.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cherry.model.base.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "User Entity")
public class User extends BaseModel {

    @Schema(description = "Username")
    @TableField(value = "`username`")
    private String username;

    @Schema(description = "Encrypted password")
    @TableField(value = "`password`")
    private String password;

    @Schema(description = "Salt")
    @TableField(value = "`salt`")
    private String salt;

    @Schema(description = "Nickname")
    @TableField(value = "`nickname`")
    private String nickname;

    @Schema(description = "Avatar URL")
    @TableField(value = "`avatar`")
    private String avatar;

    @Schema(description = "Email")
    @TableField(value = "`email`")
    private String email;
}
