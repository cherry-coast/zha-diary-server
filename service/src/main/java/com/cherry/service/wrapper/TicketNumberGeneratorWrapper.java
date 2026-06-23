package com.cherry.service.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryDateUtil;
import com.cherry.model.base.model.BaseModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年12月24日 14:26:00
 * ClassName TicketNumberGeneratorWrapper
 * packageName com.cherry.animal.service.base.wrapper
 */
@Slf4j
@SuppressWarnings("unused")
public class TicketNumberGeneratorWrapper<T extends BaseModel> {

    private final String prefix;

    private final boolean autoIncrement;

    private final IService<T> service;

    private final String dateFormat;

    public TicketNumberGeneratorWrapper(IService<T> service) {
        this.service = service;
        this.prefix = "";
        this.autoIncrement = true;
        this.dateFormat = CherryDateUtil.CherryDatePattern.CUSTOMER_PURE_DATETIME_PATTERN;
    }

    public TicketNumberGeneratorWrapper(IService<T> service, String prefix, String dateFormat) {
        this.service = service;
        this.prefix = prefix;
        this.autoIncrement = true;
        this.dateFormat = dateFormat;
    }

    public TicketNumberGeneratorWrapper(IService<T> service, String prefix, boolean autoIncrement) {
        this.service = service;
        this.prefix = prefix;
        this.autoIncrement = autoIncrement;
        this.dateFormat = CherryDateUtil.CherryDatePattern.CUSTOMER_PURE_DATETIME_PATTERN;
    }

    public TicketNumberGeneratorWrapper(IService<T> service, String prefix, boolean autoIncrement, String dateFormat) {
        this.service = service;
        this.prefix = prefix;
        this.autoIncrement = autoIncrement;
        this.dateFormat = dateFormat;
    }

    public String generatorCode(SFunction <T, ?> column) {
        String nowDate = CherryDateUtil.getNowDate(dateFormat);
        String suffixCode = String.format("%06d", ThreadLocalRandom.current().nextLong(1, 999_999));
        if (autoIncrement) {
            T one = service.getOne(
                    new QueryWrapper<T>().select("id").orderByDesc("id").last("limit 1")
            );
            log.info("get one: {}", one);
            long id = 0;
            if (Objects.nonNull(one)) {
                id = one.getId();
            }
            suffixCode = String.format("%06d", id + 1);
        }
        String code = prefix + nowDate + suffixCode;
        long count = service.count(
                new LambdaQueryWrapper<T>().eq(column, code)
                        .eq(T::getDel, false)
        );
        if (count > 1) {
            log.warn("generator code repeat, code: {}", code);
            CherryException.throwException("服务繁忙，请重试", code);
        }
        return code;
    }

    public static String generatorCode() {
        String nowDate = CherryDateUtil.getNowDate(CherryDateUtil.CherryDatePattern.CUSTOMER_PURE_DATETIME_MS_PATTERN);
        String suffixCode = String.format("%06d", ThreadLocalRandom.current().nextLong(1, 999_999));
        return nowDate + suffixCode;
    }
}
