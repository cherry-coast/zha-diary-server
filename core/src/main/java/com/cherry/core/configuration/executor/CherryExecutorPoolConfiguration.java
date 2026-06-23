package com.cherry.core.configuration.executor;

import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import com.cherry.core.properties.ExecutorPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cherry
 */
@Slf4j
@EnableAsync
@Configuration
// @ConditionalOnBean(ExecutorPoolProperties.class)
@ConditionalOnProperty(prefix="cherry.executor", name="enabled", havingValue="true", matchIfMissing=true)
@SuppressWarnings("unused")
public class CherryExecutorPoolConfiguration implements AsyncConfigurer {

    @SuppressWarnings("all")
    private static final CopyOnWriteArrayList<Runnable> WAIT_RUNNABLE_LIST = new CopyOnWriteArrayList<>();

    private ExecutorPoolProperties executorPoolProperties;

    @Autowired
    public void setExecutorPoolProperties(ExecutorPoolProperties executorPoolProperties) {
        this.executorPoolProperties = executorPoolProperties;
    }

    /**
     * customize the global thread pool for easy management
     *
     * @return {@link Executor}
     */
    @Bean("cherryExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // set the thread pool core capacity
        executor.setCorePoolSize(executorPoolProperties.getCorePoolSize());
        // set the maximum capacity of the thread pool
        executor.setMaxPoolSize(executorPoolProperties.getMaxPoolSize());
        // set the task queue length
        executor.setQueueCapacity(executorPoolProperties.getQueueCapacity());
        // set the thread timeout period
        executor.setKeepAliveSeconds(executorPoolProperties.getKeepAliveSeconds());
        // set the thread name prefix
        executor.setThreadNamePrefix(executorPoolProperties.getThreadNamePrefix());
        // set the processing policy after the task is dropped
        executor.setRejectedExecutionHandler(executorPoolProperties.getRejectedExecutionHandler());
        // wait for all tasks to finish before shutting down the thread pool
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(3 * 60);
        executor.setThreadFactory(new CustomThreadFactory());
        return executor;
    }

    public static void sleep(TimeUnit timeUnit, long time) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CherryException(BaseExceptionEnum.THREAD_ERROR.getErrorCode(), "thread sleep error !!");
        }
    }



    public static class CherryPolicy implements RejectedExecutionHandler {
        /**
         * Creates a {@code CherryPolicy}.
         */
        public CherryPolicy() { }

        /**
         *
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            WAIT_RUNNABLE_LIST.add(r);
            log.warn("Task {} rejected from {}, WAIT_RUNNABLE_LIST size: {}", r.toString(), e.toString(), WAIT_RUNNABLE_LIST.size());
        }
    }

    /**
     * The getAsyncUncaughtExceptionHandler() method returns an asynchronous uncaught exception handler.
     * The handler takes three parameters:
     * <p>
     * 1. throwable represents the exception object.
     * <p>
     * 2. method represents the method object.
     * <p>
     * 3. objects represents the invoke object.
     * <p>
     * The handler concatenates the exception information, method information, and object information in a specific format,
     * and uses the concatenated string as the parameter to call the log.error() method to print error logs.
     * The exception information is obtained by calling the throwable.toString() method,
     * the method information is obtained by calling the method.toString() method,
     * and the object information is obtained by calling the objects.getClass().getSimpleName() method.
     *
     * @return {@link AsyncUncaughtExceptionHandler}
     */
    @NonNull
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> log.error(
                executorPoolProperties.getThreadNamePrefix()
                        .concat("exception: ")
                        .concat(throwable.toString())
                        .concat(" \n method: ")
                        .concat(method.toString())
                        .concat(" \n objects: ")
                        .concat(Arrays.toString(objects))
        );
    }

    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r);
            t.setName("cherry-thread-" + threadNumber.getAndIncrement());
            t.setUncaughtExceptionHandler((thread, e) -> log.error("{} encountered an exception: {}", thread.getName(), e.getMessage()));
            return t;
        }
    }
}
