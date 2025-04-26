package com.cloud.webapp.controller;

import com.cloud.webapp.service.EmailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private EmailService emailService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String receiverEmail) {
        logger.info("POST /api/email/send hit started for request {} ", receiverEmail);
        emailService.sendEmail(receiverEmail);
        logger.info("POST /api/email/send hit completed for request {} ", receiverEmail);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
