package com.cherry.model.base.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2025年07月16日 16:14:00
 * ClassName OptionModel
 * packageName com.cherry.model.base.model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "选择框")
public class OptionModel {

    private Object key;

    private String label;

    private Object value;

}
