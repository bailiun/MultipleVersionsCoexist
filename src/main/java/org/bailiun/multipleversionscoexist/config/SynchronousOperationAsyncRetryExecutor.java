package org.bailiun.multipleversionscoexist.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@Component
public class SynchronousOperationAsyncRetryExecutor {

    private final ExecutorService executor;
    private final RetryPolicy policy;
    public SynchronousOperationAsyncRetryExecutor(ExecutorService executor, RetryPolicy policy) {
        this.executor = executor;
        this.policy = policy;
    }


    public void submitWithRetry(Callable<Void> task, Consumer<Throwable> onFailure, Runnable onSuccess) {
        executor.submit(() -> {
            int attempts = 0;
            while (attempts <= policy.getMaxRetry()) {
                try {
                    Future<Void> future = executor.submit(task);
                    future.get(policy.getTimeoutMs(), TimeUnit.MILLISECONDS);

                    if (onSuccess != null) onSuccess.run();
                    return;

                } catch (Exception ex) {
                    attempts++;
                    if (attempts > policy.getMaxRetry()) {
                        if (onFailure != null) onFailure.accept(ex);
                        return;
                    }
                    try {
                        Thread.sleep(policy.getRetryIntervalMs());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
