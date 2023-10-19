/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XkwRocketmqProperties
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月11日
 */
@ConfigurationProperties(prefix = "xkw-rocketmq")
public class XkwRocketmqProperties {

    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Producer {

        /**
         * 生产者开关，默认true
         */
        private boolean enable = true;
        /**
         * key前缀，默认使用生产者的group
         */
        private String keyPrefix;
        /**
         * 数据源类型
         */
        private DataSourceType dataSourceType = DataSourceType.MYSQL_INNODB;
        /**
         * 本地消息表数据超时天数，默认7天
         * <=0 表示不超时
         */
        private int messageOverdueDays = 7;
        /**
         * 清理过期消息的超时时间，默认10分钟，单位毫秒
         * 不能小于1000
         */
        private long cleanOverdueMessageExpireMillis = 10 * 60 * 1000L;
        /**
         * 外部重试次数，默认3次，区别于内部的自动重试
         * <=0 表示不重试
         */
        private int maxRetryTimes = 3;
        /**
         * 生产者定时扫描和发送消息的超时时间，默认10分钟，单位毫秒
         * 不能小于1000
         */
        private long scanMessageExpireMillis = 10 * 60 * 1000L;
        /**
         * 扫描失败消息的启动延迟时间，默认1分钟，单位秒
         */
        private int scanStartDelay = 60;
        /**
         * 提交事务时发送mq消息的线程池配置
         */
        private ThreadPool threadPool = new ThreadPool();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public DataSourceType getDataSourceType() {
            return dataSourceType;
        }

        public void setDataSourceType(DataSourceType dataSourceType) {
            this.dataSourceType = dataSourceType;
        }

        public int getMessageOverdueDays() {
            return messageOverdueDays;
        }

        public void setMessageOverdueDays(int messageOverdueDays) {
            this.messageOverdueDays = messageOverdueDays;
        }

        public long getCleanOverdueMessageExpireMillis() {
            return cleanOverdueMessageExpireMillis;
        }

        public void setCleanOverdueMessageExpireMillis(long cleanOverdueMessageExpireMillis) {
            this.cleanOverdueMessageExpireMillis = cleanOverdueMessageExpireMillis;
        }

        public int getMaxRetryTimes() {
            return maxRetryTimes;
        }

        public void setMaxRetryTimes(int maxRetryTimes) {
            this.maxRetryTimes = maxRetryTimes;
        }

        public long getScanMessageExpireMillis() {
            return scanMessageExpireMillis;
        }

        public void setScanMessageExpireMillis(long scanMessageExpireMillis) {
            this.scanMessageExpireMillis = scanMessageExpireMillis;
        }

        public int getScanStartDelay() {
            return scanStartDelay;
        }

        public void setScanStartDelay(int scanStartDelay) {
            this.scanStartDelay = scanStartDelay;
        }

        public ThreadPool getThreadPool() {
            return threadPool;
        }

        public void setThreadPool(ThreadPool threadPool) {
            this.threadPool = threadPool;
        }
    }

    public static class Consumer {

        /**
         * 消费者开关
         */
        private boolean enable = true;
        /**
         * 数据源类型
         */
        private DataSourceType dataSourceType = DataSourceType.MYSQL_INNODB;
        /**
         * 本地消息表数据超时天数，默认7天
         * <=0 表示不超时
         */
        private int messageOverdueDays = 7;
        /**
         * 清理过期消息的超时时间，默认10分钟，单位毫秒
         * 不能小于1000
         */
        private long cleanOverdueMessageExpireMillis = 10 * 60 * 1000L;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public DataSourceType getDataSourceType() {
            return dataSourceType;
        }

        public void setDataSourceType(DataSourceType dataSourceType) {
            this.dataSourceType = dataSourceType;
        }

        public int getMessageOverdueDays() {
            return messageOverdueDays;
        }

        public void setMessageOverdueDays(int messageOverdueDays) {
            this.messageOverdueDays = messageOverdueDays;
        }

        public long getCleanOverdueMessageExpireMillis() {
            return cleanOverdueMessageExpireMillis;
        }

        public void setCleanOverdueMessageExpireMillis(long cleanOverdueMessageExpireMillis) {
            this.cleanOverdueMessageExpireMillis = cleanOverdueMessageExpireMillis;
        }
    }

    public static class ThreadPool {

        private static final int DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors() << 1;

        /**
         * 核心线程数，默认cpu*2
         */
        private int corePoolSize = DEFAULT_POOL_SIZE;
        /**
         * 最大线程数，默认cpu*2
         */
        private int maximumPoolSize = DEFAULT_POOL_SIZE;
        /**
         * 空闲线程超时时间，毫秒，默认1小时
         * core和max相等时，这个配置没用
         */
        private long keepAliveTime = 60 * 60 * 1000L;
        /**
         * 队列长度，默认1024
         */
        private int queueSize = 1024;

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public long getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }
    }

    public enum DataSourceType {
        MYSQL_INNODB,
        OTHER
    }
}
