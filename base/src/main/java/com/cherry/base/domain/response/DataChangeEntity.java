package com.cherry.base.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cherry
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "数据变化模型")
public class DataChangeEntity {

    @Schema(description = "影响的数据id")
    private Long id;

    @Schema(description = "数据变更标志 新增修改成功都为true，其他为false")
    private Boolean changeFlag;

}
