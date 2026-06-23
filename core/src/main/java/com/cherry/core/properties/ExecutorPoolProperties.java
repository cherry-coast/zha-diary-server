package com.cherry.core.properties;

import com.cherry.base.utils.CherryStringUtil;
import com.cherry.core.configuration.executor.CherryExecutorPoolConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author cherry
 * @version 1.0.0
 */
@Data
@Component("executorPoolProperties")
@ConfigurationProperties(prefix = "cherry.executor")
public class ExecutorPoolProperties {

    /**
     * thread pool core capacity
     */
    private Integer corePoolSize;

    /**
     * the maximum capacity of the thread pool
     */
    private Integer maxPoolSize;

    /**
     * task queue length
     */
    private Integer queueCapacity;

    /**
     * excess thread lifetime
     */
    private Integer keepAliveSeconds;

    /**
     * thread name prefix
     */
    private String threadNamePrefix;

    /**
     * processing policy - the following values are available
     * <p>
     * 1: execute the task using the calling thread.
     * when the deny policy is triggered, as long as the thread pool is not down, the task is run directly using the calling thread.
     * <p>
     * 2: discard the task directly
     * <p>
     * 3: discard the oldest task in the queue and add a new task.
     * when the rejection policy is triggered, as long as the thread pool is not down, the oldest task in the blocking queue work-queue is discarded and a new task is added
     * <p>
     * the default value is throw an exception to abort the task.
     * throws a RejectedExecutionException exception message that refuses to execute.
     */
    private Integer rejectedExecutionHandler;

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return switch ((rejectedExecutionHandler == null ? 5 : rejectedExecutionHandler)) {
            case 1 -> new ThreadPoolExecutor.CallerRunsPolicy();
            case 2 -> new ThreadPoolExecutor.DiscardPolicy();
            case 3 -> new ThreadPoolExecutor.DiscardOldestPolicy();
            case 4 -> new ThreadPoolExecutor.AbortPolicy();
            default -> new CherryExecutorPoolConfiguration.CherryPolicy();
        };
    }

    public String getThreadNamePrefix() {
        return CherryStringUtil.isNotEmpty(threadNamePrefix) ? threadNamePrefix : "cherry-task-executor-";
    }

    public Integer getCorePoolSize() {
        return corePoolSize == null ? Runtime.getRuntime().availableProcessors() + 1 : corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize == null ? 2 * Runtime.getRuntime().availableProcessors() + 1 : maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity == null ? 120 : queueCapacity;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds == null ? 60 :  keepAliveSeconds;
    }
}
