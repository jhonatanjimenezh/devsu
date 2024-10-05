package com.devsu.ws_account.config.ddd;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


@Configuration
@ComponentScan(basePackages = "com.devsu.ws_account.domain.service",
        includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+Service$")},
        useDefaultFilters = false)
public class ServicesConfig {
}
