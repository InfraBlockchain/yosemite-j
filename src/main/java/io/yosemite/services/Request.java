package io.yosemite.services;

import retrofit2.Call;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Request<ResponseType> {

    private Call<ResponseType> call;

    public Request(Call<ResponseType> call) {
        this.call = call;
    }

    public ResponseType execute() throws IOException {
        return ApiServiceGenerator.executeSync(call);
    }

    public CompletableFuture<ResponseType> executeAsync() {
        return ApiServiceGenerator.executeAsync(call);
    }
}
