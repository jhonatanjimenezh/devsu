package com.devsu.ws_customer.config.ddd;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


@Configuration
@ComponentScan(basePackages = "com.devsu.ws_customer.domain.service",
        includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+Service$")},
        useDefaultFilters = false)
public class ServicesConfig {

}
