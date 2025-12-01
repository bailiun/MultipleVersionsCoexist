package org.bailiun.multipleversionscoexist.config;

public class RetryPolicy {

    /** 最大重试次数 */
    private final int maxRetry;

    /** 每次重试间隔（毫秒） */
    private final long retryIntervalMs;

    /** 超时时间（毫秒） */
    private final long timeoutMs;

    public RetryPolicy(int maxRetry, long retryIntervalMs, long timeoutMs) {
        this.maxRetry = maxRetry;
        this.retryIntervalMs = retryIntervalMs;
        this.timeoutMs = timeoutMs;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public long getRetryIntervalMs() {
        return retryIntervalMs;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }
}
