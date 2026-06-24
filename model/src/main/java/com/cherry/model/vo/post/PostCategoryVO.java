package com.cherry.model.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Post Category View Object")
public class PostCategoryVO {

    @Schema(description = "Category ID")
    private Long id;

    @Schema(description = "Category Name")
    private String name;

    @Schema(description = "Category Type (1: System, 2: Custom)")
    private Integer type;

    @Schema(description = "Number of Posts in this category")
    private Integer postCount;
}
