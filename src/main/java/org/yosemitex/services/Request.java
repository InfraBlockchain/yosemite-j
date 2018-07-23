package org.yosemitex.services;

import retrofit2.Call;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Request<Response> {

    private Call<Response> call;

    public Request(Call<Response> call) {
        this.call = call;
    }

    public Response execute() throws IOException {
        return ApiServiceGenerator.executeSync(call);
    }

    public CompletableFuture<Response> executeAsync() {
        return ApiServiceGenerator.executeAsync(call);
    }
}
