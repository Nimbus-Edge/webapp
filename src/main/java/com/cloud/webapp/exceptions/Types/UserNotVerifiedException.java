package com.cloud.webapp.exceptions.Types;

import com.cloud.webapp.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserNotVerifiedException extends BaseException {
    public UserNotVerifiedException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not authorized %s: %s", resourceName, fieldName, fieldValue),
                resourceName, fieldName, fieldValue);
    }
}