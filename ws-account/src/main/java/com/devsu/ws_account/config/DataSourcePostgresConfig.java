package com.devsu.ws_account.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourcePostgresConfig {

    private String url;
    private String driverClassName;
    private String username;
    private String password;

}
