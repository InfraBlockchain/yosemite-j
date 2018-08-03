package io.yosemite.data.remote;


import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import io.yosemite.util.StringUtils;

import java.io.IOException;

public class HostInterceptor implements Interceptor {
    private String mHost;
    private String mScheme;
    private int mPort;

    public HostInterceptor() {
    }

    public void setInterceptor(String scheme, String host, int port) {
        mScheme = scheme;
        mHost = host;
        mPort = port;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        // If new Base URL is properly formatted then replace the old one
        if (!StringUtils.isEmpty(mScheme) && !StringUtils.isEmpty(mHost)) {
            HttpUrl newUrl = original.url().newBuilder()
                    .scheme(mScheme)
                    .host(mHost)
                    .port(mPort)
                    .build();
            original = original.newBuilder()
                    .url(newUrl)
                    .build();
        }


        return chain.proceed(original);
    }
}
