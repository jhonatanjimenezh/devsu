package com.devsu.ws_customer;

import com.devsu.ws_customer.config.exception.LoadAppException;
import com.devsu.ws_customer.config.exception.SPError;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.devsu.ws_customer")
@EnableConfigurationProperties
public class WsCustomerApplication {

	public static void main(String[] args) {
		try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			throw new LoadAppException(SPError.APP_LOAD_ERROR.getErrorCode(), SPError.APP_LOAD_ERROR.getErrorMessage(), e);
		}
		SpringApplication.run(WsCustomerApplication.class, args);
	}

}
