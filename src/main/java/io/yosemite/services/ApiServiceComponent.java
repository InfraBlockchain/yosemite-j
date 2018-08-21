package io.yosemite.services;

import dagger.BindsInstance;
import dagger.Component;
import io.yosemite.LibraryModule;
import retrofit2.Retrofit;

import javax.inject.Named;
import javax.inject.Singleton;

@Component(modules = LibraryModule.class)
@Singleton
public interface ApiServiceComponent {

    Retrofit retrofit();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder baseUrl(@Named("baseUrl") String baseUrl);

        ApiServiceComponent build();
    }

}
