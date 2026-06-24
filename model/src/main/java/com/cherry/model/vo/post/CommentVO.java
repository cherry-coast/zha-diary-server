package com.cherry.model.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "Comment View Object")
public class CommentVO {

    @Schema(description = "Comment ID")
    private Long id;

    @Schema(description = "Post ID")
    private Long postId;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "User Avatar")
    private String avatar;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Parent Comment ID")
    private Long parentId;

    @Schema(description = "Reply To User ID")
    private Long replyToUserId;

    @Schema(description = "Reply To Username")
    private String replyToUsername;

    @Schema(description = "Comment Content")
    private String content;

    @Schema(description = "Like Count")
    private Integer likeCount;

    @Schema(description = "Publish Time")
    private Date insertTime;

    @Schema(description = "Children Comments (Replies)")
    private List<CommentVO> children;
}
