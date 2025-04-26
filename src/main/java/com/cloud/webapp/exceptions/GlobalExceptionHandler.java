package com.cloud.webapp.exceptions;

import com.cloud.webapp.DTO.ResponseDTO;
import com.cloud.webapp.exceptions.Types.FieldAlreadyExistsException;
import com.cloud.webapp.exceptions.Types.ResourceNotFoundException;
import com.cloud.webapp.exceptions.Types.UserNotVerifiedException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ResponseDTO> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                       WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "RESOURCE_NOT_FOUND"
        );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FieldAlreadyExistsException.class})
    public ResponseEntity<ResponseDTO> handleFieldAlreadyExistsException(FieldAlreadyExistsException exception,
                                                                         WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "FIELD_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("The HTTP method '")
                .append(ex.getMethod())
                .append("' is not allowed for this endpoint. Supported methods are: ")
                .append(Arrays.toString(ex.getSupportedHttpMethods().toArray()));

        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                errorMessage.toString(),
                request.getDescription(false),
                "METHOD_NOT_ALLOWED"
        );
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseDTO> handleServiceException(Exception exception,
                                                              WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "SERVICE_UNAVAILABLE"
        );
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errorDetailsDescription = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorDetailsDescription.put(errorName, errorMessage);
        });
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                errorDetailsDescription,
                request.getDescription(false),
                "BAD_REQUEST"
        );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ResponseDTO> handleSQLException(SQLException exception, WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "DATABASE_ERROR"
        );
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                webRequest.getDescription(false),
                "BAD_REQUEST"
        );
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                webRequest.getDescription(false),
                "USER_NOT_FOUND"
        );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getCause() instanceof UnrecognizedPropertyException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ResponseDTO> handleUserNotVerifiedException(UserNotVerifiedException ex, WebRequest webRequest) {
        ResponseDTO errorDetails = new ResponseDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                webRequest.getDescription(false),
                "USER_NOT_VERIFIED"
        );
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}