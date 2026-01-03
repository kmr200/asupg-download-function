package org.asupg.downloader.config;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class BankClientConfig {

    private final String host;
    private final String username;
    private final String password;

    @Inject
    public BankClientConfig(
            @Named("bankHost") String host,
            @Named("bankLogin") String username,
            @Named("bankPassword") String password
    ) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
