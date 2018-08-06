package io.yosemite.services;

import retrofit2.Call;

import java.util.concurrent.CompletableFuture;

public class Request<ResponseType> {

    private Call<ResponseType> call;

    public Request(Call<ResponseType> call) {
        this.call = call;
    }

    public ResponseType execute() {
        return ApiServiceGenerator.executeSync(call);
    }

    public CompletableFuture<ResponseType> executeAsync() {
        return ApiServiceGenerator.executeAsync(call);
    }
}
