package io.yosemite.services;

import io.yosemite.exception.EosApiErrorCode;
import io.yosemite.exception.YosemiteApiError;
import io.yosemite.exception.YosemiteApiException;
import io.yosemite.util.Async;
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
        return new ApiServiceExecutor(serviceClass, apiServiceComponent.retrofit());
    }

    public Service getService() {
        return service;
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    public <T> T executeSync(Call<T> call) {

        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                logger.error(call.request().toString());
                logger.error(response.toString());
                YosemiteApiError apiError = getEosApiError(response);
                throw new YosemiteApiException(apiError.getDetailedMessage(), EosApiErrorCode.get(apiError.getEosErrorCode()));
            }
        } catch (IOException e) {
            throw new YosemiteApiException(e);
        }
    }

    public <T> CompletableFuture<T> executeAsync(Call<T> call) {
        return Async.run(() -> executeSync(call));
    }

    private YosemiteApiError getEosApiError(Response<?> response) throws IOException, YosemiteApiException {
        return (YosemiteApiError) retrofit.responseBodyConverter(YosemiteApiError.class, new Annotation[0])
                .convert(response.errorBody());
    }
}
