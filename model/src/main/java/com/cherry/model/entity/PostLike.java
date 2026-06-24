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
@TableName("post_like")
@Schema(description = "Post Like Entity")
public class PostLike extends BaseModel {

    @Schema(description = "Post ID")
    @TableField(value = "`post_id`")
    private Long postId;

    @Schema(description = "User ID (null means anonymous)")
    @TableField(value = "`user_id`")
    private Long userId;

    @Schema(description = "IP Address (For anonymous likes)")
    @TableField(value = "`ip_address`")
    private String ipAddress;
}
