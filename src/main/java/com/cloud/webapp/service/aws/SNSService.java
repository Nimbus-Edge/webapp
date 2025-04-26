package com.cloud.webapp.service.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SNSService {

    @Value("${sns.topic.name}")
    private String snsTopicArn;

    @Autowired
    private AmazonSNS snsClient;
    private static final Logger logger = LoggerFactory.getLogger(SNSService.class);

    public void publishToTopic(String message) {
        logger.info("Preparing to publish message to SNS topic: {}", snsTopicArn);
        logger.info("Message content: {}", message);
        try {
            PublishRequest publishRequest = new PublishRequest(snsTopicArn, message);
            PublishResult result = snsClient.publish(publishRequest);
            logger.info("Message successfully sent to SNS topic. Message ID: {}", result.getMessageId());

        } catch (Exception e) {
            logger.error("Failed to publish message to SNS topic", e);
        }
    }
}
