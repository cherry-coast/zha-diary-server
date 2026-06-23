package com.cherry.web.base.aop;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.cherry.base.domain.constant.StringConstant;
import com.cherry.base.domain.model.InterfaceLogModel;
import com.cherry.base.domain.threadlocal.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cherry
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
public class InterfaceLogAspect {

    @Pointcut("execution( * com.cherry.*.api.*.*(..))")
    public void interfaceLogAspect() {
    }

    @Around(value = "interfaceLogAspect()")
    public Object recordWebLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userName = Objects.isNull(UserContext.getUser()) ? "" : UserContext.getUser().getName();
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Operation annotation = method.getAnnotation(Operation.class);
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        String requestUrl = request.getRequestURL().toString();
        InterfaceLogModel webLog = InterfaceLogModel
                .builder()
                .basePath(StrUtil.removeSuffix(requestUrl, URLUtil.url(requestUrl).getPath()))
                .description(annotation == null ? "no desc" : annotation.summary())
                .ip(request.getRemoteAddr())
                .parameter(getMethodParameter(method, proceedingJoinPoint.getArgs()))
                .method(className + "." + method.getName())
                .recodeTime(System.currentTimeMillis())
                .uri(request.getRequestURI())
                .url(request.getRequestURL().toString())
                .build();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result;
        try {
            result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            webLog.setResult(result);
        } finally {
            stopWatch.stop();
            if (Objects.isNull(webLog.getResult())) {
                webLog.setResult("request error !!!");
            }
            webLog.setUsername(userName);
            webLog.setSpendTime(stopWatch.getTotalTimeMillis());
            log.info("interface args log --> {}", webLog);
        }
        return result;
    }

    /**
     * gets the execution parameters of the method
     *
     * @param method 方法
     * @param args   参数
     * @return {"key_参数的名称":"value_参数的值"}
     */
    private Object getMethodParameter(Method method, Object[] args) {
        Map<String, Object> methodParametersWithValues = new HashMap<>(16);
        StandardReflectionParameterNameDiscoverer standardReflectionParameterNameDiscoverer =
                new StandardReflectionParameterNameDiscoverer();
        String[] parameterNames = standardReflectionParameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < Objects.requireNonNull(parameterNames).length; i++) {
            if (parameterNames[i].equals(StringConstant.PASSWORD) || parameterNames[i].equals(StringConstant.FILE)) {
                methodParametersWithValues.put(parameterNames[i], "受限的支持类型");
            } else {
                methodParametersWithValues.put(parameterNames[i], args[i]);
            }
        }
        return methodParametersWithValues;
    }



    @PostConstruct
    public void init() {
        log.info("aopCodeSand Bean is initialized.");
    }

}
