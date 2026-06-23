package com.cherry.database.base.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.base.utils.CherryDateUtil;
import com.cherry.core.properties.CherryDatabaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 1.可以用来分析SQL执行效率 2.可以用来获取实际执行的SQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        ),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
        ),
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}
        )
})
@ConditionalOnBean({CherryDatabaseProperties.class})
public class MybatisPlusSqlPrintInterceptor implements Interceptor {

    private final CherryDatabaseProperties cherryDatabaseProperties;

    private final ThreadPoolTaskExecutor cherryExecutor;

    @Override
    public Object intercept(Invocation invocation) {
        Object result;
        long startTime = System.currentTimeMillis();
        try {
            result = invocation.proceed();

            long sqlCostTime = System.currentTimeMillis() - startTime;
            if (cherryDatabaseProperties.isSqlLogEnable() && (sqlCostTime > cherryDatabaseProperties.getSqlExecTime())) {
                long size = result instanceof Collection<?> ? ((Collection<?>) result).size() : Convert.toLong(result);
                MybatisPlusSqlLogUtil.excAsync(cherryExecutor, invocation, size, sqlCostTime);
            }

        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                log.error(targetException.getMessage());
                throw new CherryException(
                        BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                        e.getStackTrace(),
                        targetException.getMessage()
                );
            }
            throw new CherryException(
                    BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(),
                    e.getStackTrace(),
                    "sql exec log print error"
            );
        }
        return result;
    }
}

@Slf4j
class MybatisPlusSqlLogUtil {
    private static final String SQL_LOG = """
        \n
        ╔════════════════════════════════ SQL INFO ════════════════════════════════╗
        ║  执行耗时: {}ms
        ║  {} 行数: {}
        ║  执行方法: {}
        ║  执行语句: {}
        ╚══════════════════════════════════════════════════════════════════════════╝
    """;

    public static void excAsync(
            ThreadPoolTaskExecutor cherryExecutor, Invocation invocation, Object result, long sqlCostTime
    ) {
        cherryExecutor.execute(() -> paramBuild(invocation, result, sqlCostTime));
    }

    public static void paramBuild(Invocation invocation, Object result, long sqlCostTime) {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        String sql = getExecSql(configuration, boundSql);
        formatSqlLog(mappedStatement.getSqlCommandType(), sqlId, sql, sqlCostTime, result);
    }

    private static String getExecSql(Configuration configuration, BoundSql boundSql) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (StrUtil.isBlank(sql)) {
            return "";
        }

        // 去掉换行符
        sql = sql.replaceAll("[\\s\n ]+", " ");

        // 填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings.isEmpty() || parameterObject == null) {
            return sql;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            sql = replacePlaceholder(sql, parameterObject);
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                if (metaObject.hasGetter(propertyName)) {
                    Object obj = metaObject.getValue(propertyName);
                    sql = replacePlaceholder(sql, obj);
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    Object obj = boundSql.getAdditionalParameter(propertyName);
                    sql = replacePlaceholder(sql, obj);
                }
            }
        }
        return sql;
    }

    private static String replacePlaceholder(String sql, Object parameterObject) {
        String result;
        if (Objects.isNull(parameterObject)) {
            result = "NULL";
        } else if (parameterObject instanceof String) {
            result = String.format("'%s'", parameterObject);
        } else if (parameterObject instanceof Date) {
            result = String.format(
                    "'%s'",
                    CherryDateUtil.format(
                            parameterObject,
                            CherryDateUtil.CherryDatePattern.NORM_DATETIME_MS_PATTERN
                    )
            );
        } else {
            result = parameterObject.toString();
        }
        return sql.replaceFirst("\\?", result);
    }

    private static void formatSqlLog(
            SqlCommandType sqlCommandType, String sqlId, String sql, long costTime, Object obj
    ) {
        String str = "";

        if (
                sqlCommandType == SqlCommandType.UPDATE ||
                sqlCommandType == SqlCommandType.INSERT ||
                sqlCommandType == SqlCommandType.DELETE
        ) {
            str = "影响";
        }
        if (sqlCommandType == SqlCommandType.SELECT) {
            str = "结果";
        }
        log.info(SQL_LOG, costTime, str, obj, sqlId, sql);
    }

}
 