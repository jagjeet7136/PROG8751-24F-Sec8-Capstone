package com.app.ecommerce;

import com.app.ecommerce.constants.SecurityConstants;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class EcommerceApplication {

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private static void loadEnvToSystemProperties() {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		System.setProperty(SecurityConstants.SENDGRID_API_KEY,
				dotenv.get(SecurityConstants.SENDGRID_API_KEY));
		System.setProperty(SecurityConstants.STRIPE_WEBHOOK_KEY,
				dotenv.get(SecurityConstants.STRIPE_WEBHOOK_KEY));
	}

	public static void main(String[] args) {
		loadEnvToSystemProperties();
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
