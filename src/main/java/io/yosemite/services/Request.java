package io.yosemite.services;

import retrofit2.Call;

import java.util.concurrent.CompletableFuture;

public class Request<ResponseType> {

    private final Call<ResponseType> call;
    private final ApiServiceExecutor apiServiceExecutor;
    private final boolean isChainApi;

    Request(Call<ResponseType> call,
            ApiServiceExecutor apiServiceExecutor) {
        this(call, apiServiceExecutor, true);
    }

    Request(Call<ResponseType> call,
            ApiServiceExecutor apiServiceExecutor,
            boolean isChainApi) {
        this.call = call;
        this.apiServiceExecutor = apiServiceExecutor;
        this.isChainApi = isChainApi;
    }

    public ResponseType execute() {
        return (ResponseType) this.apiServiceExecutor.executeSync(call, isChainApi);
    }

    public CompletableFuture<ResponseType> executeAsync() {
        return apiServiceExecutor.executeAsync(call, null);
    }

    public CompletableFuture<ResponseType> executeAsync(boolean isChainApi) {
        return apiServiceExecutor.executeAsync(call, null, isChainApi);
    }

    public CompletableFuture<ResponseType> executeAsync(Object attachment) {
        return apiServiceExecutor.executeAsync(call, attachment, true);
    }

    public CompletableFuture<ResponseType> executeAsync(Object attachment, boolean isChainApi) {
        return apiServiceExecutor.executeAsync(call, attachment, isChainApi);
    }
}
