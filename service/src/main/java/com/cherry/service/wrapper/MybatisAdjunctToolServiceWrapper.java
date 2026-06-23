package com.cherry.service.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryStringUtil;
import com.cherry.model.base.model.BaseModel;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author cherry
 * @version 1.0.0
 * Description mybatis 辅助工具包装接口
 * Date 2024年07月29日 10:00:00
 * ClassName MybatisAdjunctToolServiceWrapper
 * packageName com.cherry.business.base
 */
@SuppressWarnings("unused")
public interface MybatisAdjunctToolServiceWrapper<T extends BaseModel> extends IService<T> {

    int batchSize = 1000;

    default T checkThisDataIsExists(Long id, String message) {
        T t = this.getById(id);
        if (Objects.isNull(t) || t.getDel()) {
            throw new CherryException(
                    BaseExceptionEnum.FAIL.getErrorCode(),
                    CherryStringUtil.isNotBlank(message) ?
                            message : String.format("this query id: %s data is not exists", id)
            );
        }
        return t;
    }

    default void checkDataIsRepeat(Consumer<LambdaQueryWrapper<T>> condition, String message) {
        long count = this.count(new LambdaQueryWrapper<T>().and(condition));
        if (count > 0) {
            throw new CherryException(
                    BaseExceptionEnum.FAIL.getErrorCode(),
                    message
            );
        }
    }

    default void throwCustomerBizException(String message) {
        throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), message);
    }

}
