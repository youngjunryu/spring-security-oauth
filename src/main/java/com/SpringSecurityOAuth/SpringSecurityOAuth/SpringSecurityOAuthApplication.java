package com.SpringSecurityOAuth.SpringSecurityOAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SpringSecurityOAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityOAuthApplication.class, args);
	}

}
