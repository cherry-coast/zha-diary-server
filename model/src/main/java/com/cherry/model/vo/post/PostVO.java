package com.cherry.model.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "Post View Object")
public class PostVO {

    @Schema(description = "Post ID")
    private Long id;

    @Schema(description = "User ID (null means anonymous)")
    private Long userId;

    @Schema(description = "User Avatar")
    private String avatar;

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Post Content")
    private String content;

    @Schema(description = "Appearance (1-4)")
    private Integer appearance;

    @Schema(description = "Category Names")
    private List<String> categoryNames;

    @Schema(description = "Like Count")
    private Integer likeCount;

    @Schema(description = "Comment Count")
    private Integer commentCount;

    @Schema(description = "Whether current user liked it")
    private Boolean isLiked;

    @Schema(description = "Publish Time")
    private Date insertTime;
}
