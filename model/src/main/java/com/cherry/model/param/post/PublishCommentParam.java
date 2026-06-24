package com.cherry.model.param.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Publish Comment Parameter")
public class PublishCommentParam {

    @Schema(description = "Post ID")
    private Long postId;

    @Schema(description = "Comment Content")
    private String content;

    @Schema(description = "Parent Comment ID (optional)")
    private Long parentId;

    @Schema(description = "Reply To User ID (optional)")
    private Long replyToUserId;
}
