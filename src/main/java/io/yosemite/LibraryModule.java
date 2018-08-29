package io.yosemite;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import io.yosemite.data.remote.HostInterceptor;
import io.yosemite.util.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Module
public class LibraryModule {

    @Provides
    @Singleton
    static Gson providesGson() {
        return Utils.createYosemiteJGsonBuilder().create();
    }

    @Provides
    @Singleton
    static Converter.Factory providesGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    static CallAdapter.Factory providesRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    static HostInterceptor providesHostInterceptor() {
        return new HostInterceptor();
    }

    @Provides
    @Singleton
    static HttpLoggingInterceptor providesHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    static OkHttpClient providesOkHttpClient(HostInterceptor hostInterceptor, HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(new HostInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    static Retrofit providesRetrofit(OkHttpClient okHttpClient,
                                     Converter.Factory converterFactory,
                                     CallAdapter.Factory adapterFactory,
                                     @Named("baseUrl") String baseUrl
    ) {
        return new Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .baseUrl(baseUrl).build();
    }
}
