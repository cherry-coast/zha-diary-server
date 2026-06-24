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
@TableName("post_category")
@Schema(description = "Post Category Entity")
public class PostCategory extends BaseModel {

    @Schema(description = "Category Name")
    @TableField(value = "`name`")
    private String name;

    @Schema(description = "Category Type (1: System Default, 2: User Custom)")
    @TableField(value = "`type`")
    private Integer type;

    @Schema(description = "Sort Order")
    @TableField(value = "`sort`")
    private Integer sort;
}
