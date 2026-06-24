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
@TableName("post")
@Schema(description = "Post (Melon) Entity")
public class Post extends BaseModel {

    @Schema(description = "User ID (null means anonymous)")
    @TableField(value = "`user_id`")
    private Long userId;

    @Schema(description = "Post Content (Rich Text)")
    @TableField(value = "`content`")
    private String content;

    @Schema(description = "Melon Appearance (1-4)")
    @TableField(value = "`appearance`")
    private Integer appearance;

    @Schema(description = "Category Name")
    @TableField(value = "`category_name`")
    private String categoryName;

    @Schema(description = "Like Count")
    @TableField(value = "`like_count`")
    private Integer likeCount;

    @Schema(description = "Comment Count")
    @TableField(value = "`comment_count`")
    private Integer commentCount;

    @Schema(description = "IP Address (For Anonymous Posts)")
    @TableField(value = "`ip_address`")
    private String ipAddress;
}
