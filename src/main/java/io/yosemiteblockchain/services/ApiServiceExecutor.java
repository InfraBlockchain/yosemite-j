package io.yosemiteblockchain.services;

import io.yosemiteblockchain.data.remote.chain.SignedTransaction;
import io.yosemiteblockchain.exception.YosemiteApiError;
import io.yosemiteblockchain.exception.YosemiteApiException;
import io.yosemiteblockchain.util.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;

public final class ApiServiceExecutor<Service> {

    private final static Logger logger = LoggerFactory.getLogger(ApiServiceExecutor.class);

    private Service service;

    private Retrofit retrofit;

    private ApiServiceExecutor(Class<Service> serviceClass, Retrofit retrofit) {
        this.retrofit = retrofit;
        this.service = this.retrofit.create(serviceClass);
    }

    public static <S> ApiServiceExecutor<S> create(Class<S> serviceClass, String baseUrl) {
        ApiServiceComponent apiServiceComponent = DaggerApiServiceComponent.builder().baseUrl(baseUrl).build();
        return new ApiServiceExecutor<>(serviceClass, apiServiceComponent.retrofit());
    }

    Service getService() {
        return service;
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    <T> T executeSync(Call<T> call) {
        return executeSync(call, null, true);
    }

    <T> T executeSync(Call<T> call, boolean isChainApi) {
        return executeSync(call, null, isChainApi);
    }

    <T> T executeSync(Call<T> call, Object attachment, boolean isChainApi) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                logger.debug(call.request().toString());
                logger.debug(response.toString());
                if (isChainApi) {
                    YosemiteApiException yosemiteApiException = new YosemiteApiException(getEosApiError(response));
                    if (attachment instanceof SignedTransaction) {
                        SignedTransaction signedTransaction = (SignedTransaction) attachment;
                        yosemiteApiException.setTransactionId(signedTransaction.getId());
                    }
                    throw yosemiteApiException;
                } else {
                    YosemiteApiError error = new YosemiteApiError();
                    error.setCode(response.code());
                    error.setMessage(response.message());
                    throw new YosemiteApiException(error);
                }
            }
        } catch (IOException e) {
            throw new YosemiteApiException(e);
        }
    }

    <T> CompletableFuture<T> executeAsync(Call<T> call, Object attachment) {
        return executeAsync(call, attachment, true);
    }

    <T> CompletableFuture<T> executeAsync(Call<T> call, Object attachment, boolean isChainApi) {
        return Async.run(() -> executeSync(call, attachment, isChainApi));
    }

    private YosemiteApiError getEosApiError(Response<?> response) throws IOException {
        return (YosemiteApiError) retrofit.responseBodyConverter(YosemiteApiError.class, new Annotation[0])
                .convert(response.errorBody());
    }
}
