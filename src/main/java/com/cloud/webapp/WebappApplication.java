package com.cloud.webapp;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@OpenAPIDefinition(info = @Info(title = "Spring Boot REST API Documentation",
		description = "RESTFUL Spring Boot Web App Service",
		version = "v1.0",
		contact = @Contact(name = "Deepak Viswanadha", email = "test@test.com", url = "test.com"),
		license = @License(name = "Apache 2.0", url = "test.com")),
		externalDocs = @ExternalDocumentation(description = "Spring Boot and System Design doc", url = "test.com"))
@SpringBootApplication
@EnableFeignClients
@ImportAutoConfiguration({ FeignAutoConfiguration.class })
public class WebappApplication {

	private static final Logger logger = LoggerFactory.getLogger(WebappApplication.class);

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		String awsAccessKey = dotenv.get("AWS_ACCESS_KEY", "");
		String awsSecretKey = dotenv.get("AWS_SECRET_KEY", "");

		if (awsAccessKey.isEmpty()) {
			logger.warn("AWS Access Key is not set.");
		} else {
			logger.info("AWS Access Key is set.");
		}
		if (awsSecretKey.isEmpty()) {
			logger.warn("AWS Secret Key is not set.");
		} else {
			logger.info("AWS Secret Key is set.");
		}

		System.setProperty("aws.accessKeyId", awsAccessKey);
		System.setProperty("aws.secretAccessKey", awsSecretKey);
		System.setProperty("aws.region", dotenv.get("AWS_REGION"));
		System.setProperty("aws.bucketName", dotenv.get("AWS_BUCKET_NAME"));
		System.setProperty("sendgrid.api.key", dotenv.get("SENDGRID_API_KEY"));
		System.setProperty("sendgrid.from.email",dotenv.get("SENDGRID_FROM_EMAIL"));
		System.setProperty("sns.topic.name", dotenv.get("SNS_TOPIC_NAME"));

		SpringApplication.run(WebappApplication.class, args);
	}
}
