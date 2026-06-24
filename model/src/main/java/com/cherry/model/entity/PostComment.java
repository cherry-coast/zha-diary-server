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
@TableName("post_comment")
@Schema(description = "Post Comment Entity")
public class PostComment extends BaseModel {

    @Schema(description = "Post ID")
    @TableField(value = "`post_id`")
    private Long postId;

    @Schema(description = "User ID (Must be logged in)")
    @TableField(value = "`user_id`")
    private Long userId;

    @Schema(description = "Parent Comment ID (for nested replies)")
    @TableField(value = "`parent_id`")
    private Long parentId;

    @Schema(description = "Reply To User ID")
    @TableField(value = "`reply_to_user_id`")
    private Long replyToUserId;

    @Schema(description = "Comment Content")
    @TableField(value = "`content`")
    private String content;

    @Schema(description = "Like Count")
    @TableField(value = "`like_count`")
    private Integer likeCount;
}
