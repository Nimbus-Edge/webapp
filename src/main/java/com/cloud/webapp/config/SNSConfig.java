package com.cloud.webapp.config;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SNSConfig {

    @Value("${aws.region}") String region;

    @Bean
    public AmazonSNS amazonSNS() {
        return AmazonSNSClient.builder()
                .withRegion(region)
                .build();
    }
}
