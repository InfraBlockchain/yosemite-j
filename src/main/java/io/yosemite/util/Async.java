package io.yosemite.util;

import java.util.concurrent.*;

import static java.util.concurrent.ForkJoinPool.defaultForkJoinWorkerThreadFactory;

public class Async {
    private static final int MAX_CAP      = 0x7fff;        // max #workers - 1

    private static final ExecutorService executor = new ForkJoinPool(
            Math.min(MAX_CAP, getCpuCount()), defaultForkJoinWorkerThreadFactory, null, true);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Async::shutdown));
    }

    public static <T> CompletableFuture<T> run(Callable<T> callable) {
        CompletableFuture<T> result = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                result.complete(callable.call());
            } catch (Throwable t) {
                result.completeExceptionally(t);
            }
        }, executor);

        return result;
    }

    private static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static void shutdown() {
        Async.executor.shutdown();
        try {
            if (!Async.executor.awaitTermination(60, TimeUnit.SECONDS)) {
                Async.executor.shutdownNow();
                if (!Async.executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            Async.executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
