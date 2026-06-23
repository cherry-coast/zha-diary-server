package com.cherry.model.base.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年07月25日 15:06:00
 * ClassName BaseModel
 * packageName com.cherry.domain.model
 */
@Data
@ToString
public class BaseModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "`insert_time`", fill = FieldFill.INSERT)
    private Timestamp insertTime;

    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;

    @TableLogic
    @TableField(value = "`del`", fill = FieldFill.INSERT)
    private Boolean del;

    @TableField(value = "`remark`")
    private String remark;

}
