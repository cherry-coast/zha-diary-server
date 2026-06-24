package com.cherry.model.param.post;

import com.cherry.model.base.page.CherryPageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Post Page Parameter")
public class PostPageParam extends CherryPageRequest {

    @Schema(description = "Category Name to filter by")
    private String categoryName;
}
