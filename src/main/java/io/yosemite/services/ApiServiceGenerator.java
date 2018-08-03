package io.yosemite.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.yosemite.data.remote.HostInterceptor;
import io.yosemite.data.util.GsonEosTypeAdapterFactory;
import io.yosemite.exception.EosApiError;
import io.yosemite.exception.EosApiErrorCode;
import io.yosemite.exception.EosApiException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.yosemite.util.Async;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;

public class ApiServiceGenerator {

    final static Logger logger = LoggerFactory.getLogger(ApiServiceGenerator.class);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new HostInterceptor());

    public static Gson gson = new GsonBuilder().registerTypeAdapterFactory(new GsonEosTypeAdapterFactory()).excludeFieldsWithoutExposeAnnotation().create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(httpClient.build());

    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {

        builder.baseUrl(baseUrl);
        builder.client(httpClient.build());
        retrofit = builder.build();

        return retrofit.create(serviceClass);
    }

    /**
     * Execute a REST call and block until the response is received.
     */
    public static <T> T executeSync(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                logger.error(call.request().toString());
                logger.error(response.toString());
                EosApiError apiError = getEosApiError(response);
                throw new EosApiException(apiError.getDetailedMessage(), EosApiErrorCode.get(apiError.getEosErrorCode()));
            }
        } catch (IOException e) {
            throw new EosApiException(e);
        }
    }

    public static <T> CompletableFuture<T> executeAsync(Call<T> call) {
        return Async.run(() -> executeSync(call));
    }

    /**
     * Extracts and converts the response error body into an object.
     */
    private static EosApiError getEosApiError(Response<?> response) throws IOException, EosApiException {
        return (EosApiError) retrofit.responseBodyConverter(EosApiError.class, new Annotation[0])
                .convert(response.errorBody());
    }
}
