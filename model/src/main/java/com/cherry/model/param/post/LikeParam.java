package com.cherry.model.param.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Toggle Like Parameter")
public class LikeParam {

    @Schema(description = "Target ID (Post ID or Comment ID)")
    private Long targetId;

    @Schema(description = "Target Type (1: Post)")
    private Integer targetType;
}
