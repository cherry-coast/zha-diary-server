package com.cherry.database.base.handler;

import com.cherry.base.domain.response.CherryResponseEntity;
import com.cherry.base.utils.CherryCollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年12月25日 10:02:00
 * ClassName MybatisExceptionHandler
 * packageName com.cherry.animal.database.base.mybatis.handler
 */
@Slf4j
@RestControllerAdvice
public class MybatisExceptionHandler {


    /**
     * handler Exception
     *
     * @param e {@link MyBatisSystemException}
     * @return {@link CherryResponseEntity}
     */
    @ExceptionHandler(value = MyBatisSystemException.class)
    public CherryResponseEntity<String> handleMyBatisSystemException(MyBatisSystemException e) {
        log.error(
                "mybatis system exception handler, exception msg: {}, exception stack trace: {}",
                e.getCause().getMessage(),
                CherryCollectionUtil.arrayToString(e.getStackTrace())
        );
        return CherryResponseEntity.fail("处理失败，请稍后重试");
    }

}
