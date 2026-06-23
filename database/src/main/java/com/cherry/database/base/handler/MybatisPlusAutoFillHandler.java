package com.cherry.database.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年07月25日 15:43:00
 * ClassName MybatisPlusAutoFillHandler
 * packageName com.cherry.handler.mybatis
 */
@Slf4j
@Component
public class MybatisPlusAutoFillHandler implements MetaObjectHandler {

    private static final String INSERT_TIME = "insertTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String DEL = "del";

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName(UPDATE_TIME, Timestamp.valueOf(now), metaObject);
        if (Objects.isNull(metaObject.getValue(INSERT_TIME))) {
            this.setFieldValByName(INSERT_TIME, Timestamp.valueOf(now), metaObject);
        }
        if (Objects.isNull(metaObject.getValue(DEL))) {
            this.setFieldValByName(DEL, Boolean.FALSE, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(UPDATE_TIME, Timestamp.valueOf(LocalDateTime.now()), metaObject);
    }
}
