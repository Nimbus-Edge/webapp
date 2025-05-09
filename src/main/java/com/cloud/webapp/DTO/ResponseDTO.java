package com.cloud.webapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private LocalDateTime timestamp;
    private Object message;
    private String path;
    private String errorCode;

}