package io.yosemite.services;

import retrofit2.Call;

import java.util.concurrent.CompletableFuture;

public class Request<ResponseType> {

    private Call<ResponseType> call;
    private ApiServiceExecutor apiServiceExecutor;

    public Request(Call<ResponseType> call,
                   ApiServiceExecutor apiServiceExecutor) {
        this.call = call;
        this.apiServiceExecutor = apiServiceExecutor;
    }

    public ResponseType execute() {
        return (ResponseType) this.apiServiceExecutor.executeSync(call);
    }

    public CompletableFuture<ResponseType> executeAsync() {
        return this.apiServiceExecutor.executeAsync(call, null);
    }

    public CompletableFuture<ResponseType> executeAsync(Object attachment) {
        return this.apiServiceExecutor.executeAsync(call, attachment);
    }
}
