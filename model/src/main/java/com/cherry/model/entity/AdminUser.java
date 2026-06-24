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
@TableName("admin_user")
@Schema(description = "Admin User Entity")
public class AdminUser extends BaseModel {

    @Schema(description = "Admin Username")
    @TableField(value = "`username`")
    private String username;

    @Schema(description = "Encrypted Password")
    @TableField(value = "`password`")
    private String password;

    @Schema(description = "Status (0-Disabled, 1-Enabled)")
    @TableField(value = "`status`")
    private Integer status;

}
