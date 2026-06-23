package com.cherry.base.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

@Slf4j
@SuppressWarnings("unused")
public class SnowflakeUtil {

    // ==============================Fields===========================================

    /** 开始时间戳 (2000-01-01 00:00:00) */
    private static final long TWEPOCH = 946656000000L;

    /** 机器 ID 所占的位数 */
    private static final long WORKER_ID_BITS = 5L;

    /** 数据标识 ID 所占的位数 */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /** 支持的最大机器 ID 值 */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /** 支持的最大数据中心 ID 值 */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /** 序列在 ID 中占的位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 机器 ID 左移位数 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /** 数据中心 ID 左移位数 */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /** 时间戳左移位数 */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /** 序列掩码，确保序列不会超出范围 */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /** 步长，用于时钟回拨时的调整 */
    private static final long STEP_SIZE = 1024;

    /** unsigned int 最大值 */
    private static final long UINT_MAX_VALUE = 0xffffffffL;

    /** 工作机器 ID
     * -- GETTER --
     *  获取机器 ID
     */
    @Getter
    private long workerId;

    /** 数据中心 ID
     * -- GETTER --
     * 获取数据中心 ID
     */
    @Getter
    private long dataCenterId;

    /** 毫秒内序列 */
    private long sequence = 0L;

    /** 毫秒内序列基数 [0, 1024, 2048, 3072] */
    private long basicSequence = 0L;

    /** 上次生成 ID 的时间戳 */
    private long lastTimestamp = -1L;

    /** 工作模式 */
    private final WorkMode workMode;

    public enum WorkMode { NON_SHARED, RATE_1024, RATE_4096 }

    // ==============================Constructors=====================================

    public SnowflakeUtil() {
        this(0, 0, WorkMode.RATE_4096);
    }

    public SnowflakeUtil(long workerId, long dataCenterId) {
        this(workerId, dataCenterId, WorkMode.RATE_4096);
    }

    public SnowflakeUtil(long workerId, long dataCenterId, WorkMode workMode) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                MessageFormat.format("Worker ID must be between 0 and {0}", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(
                MessageFormat.format("DataCenter ID must be between 0 and {0}", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.workMode = workMode;
    }

    // ==============================Methods==========================================

    /** 获取下一个 ID（线程安全） */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            handleClockBackward(timestamp);
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = basicSequence;
        }

        lastTimestamp = timestamp;

        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /** 等待直到下一毫秒 */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp;
        do {
            timestamp = timeGen();
        } while (timestamp <= lastTimestamp);
        return timestamp;
    }

    /** 获取当前时间戳（毫秒） */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /** 处理时钟回拨 */
    private void handleClockBackward(long timestamp) {
        if (workMode == WorkMode.NON_SHARED) {
            nonSharedClockBackwards(timestamp);
        } else if (workMode == WorkMode.RATE_1024) {
            rate1024ClockBackwards(timestamp);
        } else {
            throw new RuntimeException(
                MessageFormat.format("Clock moved backwards. Refusing to generate ID for {0} ms", 
                                     lastTimestamp - timestamp));
        }
    }

    /** 非共享模式下处理时钟回拨 */
    private void nonSharedClockBackwards(long timestamp) {
        log.warn("Clock moved backwards. Attempting recovery...");
        if (++workerId > MAX_WORKER_ID) {
            workerId = 0;
            dataCenterId = (dataCenterId + 1) % (MAX_DATA_CENTER_ID + 1);
        }
        lastTimestamp = timestamp;
    }

    /** 每毫秒最多 1024 个 ID 的模式下处理时钟回拨 */
    private void rate1024ClockBackwards(long timestamp) {
        log.warn("Clock moved backwards. Adjusting sequence...");
        if (basicSequence + STEP_SIZE > SEQUENCE_MASK) {
            throw new RuntimeException("Sequence overflow during clock backward adjustment");
        }
        basicSequence += STEP_SIZE;
        lastTimestamp = timestamp;
    }

    /** 设置指定位为 1 */
    private long setSpecifiedBitTo1(long value, long index) {
        return value | (1L << index);
    }

    /** 设置指定位为 0 */
    private long setSpecifiedBitTo0(long value, long index) {
        return value & ~(1L << index);
    }

    /** 获取指定位的值 */
    private int getSpecifiedBit(long value, long index) {
        return (value & (1L << index)) == 0 ? 0 : 1;
    }
}
