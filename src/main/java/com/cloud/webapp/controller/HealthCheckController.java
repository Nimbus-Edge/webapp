package com.cloud.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Tag(
        name="Health Check",
        description = "Endpoints to check health status of various services under webapp service"
)
@RestController
@AllArgsConstructor
@RequestMapping("/healthz")
public class HealthCheckController {

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Operation(summary = "Fetch the health",
            description = "Get info about the health of all services in webapp service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Health checks are passed, Service is healthy"),
            @ApiResponse(responseCode = "503", description = "Health Check Failure - One/more health checks failed, Service is not healthy"),
            @ApiResponse(responseCode = "405", description = "Bad Request - Method Not Allowed"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Payload or Path Params are not allowed"),
    })
    @GetMapping
    public ResponseEntity<Void> healthCheck(
            @RequestHeader(value = HttpHeaders.CONTENT_LENGTH, required = false) String contentLength,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            WebRequest webRequest) throws SQLException {
        logger.info("GET /healthz hit started");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        responseHeaders.setPragma("no-cache");

        // Check if there are any query parameters
        if (!webRequest.getParameterMap().isEmpty()) {
            logger.error("Query parameters are not allowed for {} endpoint", "/healthz");
            logger.info("GET /healthz hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        if (contentLength != null && Integer.parseInt(contentLength) > 0) {
            logger.error("Payload is not allowed for {} endpoint", "/healthz");
            logger.info("GET /healthz hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        if (contentType != null && !contentType.equals(MediaType.ALL_VALUE)) {
            logger.error("Content type is not allowed for {} endpoint", "/healthz");
            logger.info("GET /healthz hit completed successfully");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .build();
        }

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                logger.info("GET /healthz hit completed successfully, all services are healthy");
                return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).build();
            } else {
                //to:do : add more checks for every service and not hardcoded.
                logger.error("DB Service is down for {} endpoint", "/healthz");
                logger.info("GET /healthz hit completed successfully");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(responseHeaders).build();
            }
        }
    }
}
