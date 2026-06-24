package com.cherry.model.param.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Publish Post Parameter")
public class PublishPostParam {

    @Schema(description = "Post Content (Rich Text)")
    private String content;

    @Schema(description = "Melon Appearance (1-4)")
    private Integer appearance;

    @Schema(description = "Category Names (Optional, Multiple)")
    private List<String> categoryNames;
}
