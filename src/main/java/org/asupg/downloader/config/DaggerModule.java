package org.asupg.downloader.config;

import dagger.Module;
import dagger.Provides;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.cookie.CookieStore;
import tools.jackson.databind.ObjectMapper;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DaggerModule {

    @Provides
    @Singleton
    @Named("bankHost")
    String provideBankClientHost() {
        return getEnv("BANK_CLIENT_HOST");
    }

    @Provides
    @Singleton
    @Named("bankLogin")
    String provideBankClientLogin() {
        return getEnv("BANK_CLIENT_LOGIN");
    }

    @Provides
    @Singleton
    @Named("bankPassword")
    String provideBankClientPassword() {
        return getEnv("BANK_CLIENT_PASSWORD");
    }

    @Provides
    @Singleton
    CloseableHttpClient provideHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .disableAutomaticRetries()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    @Provides
    @Singleton
    CookieStore provideCookieStore() {
        return new BasicCookieStore();
    }

    @Provides
    @Singleton
    ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    private static String getEnv(String key) {
        String value = System.getProperty(key, System.getenv(key));
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing env variable: " + key);
        }
        return value;
    }

}
