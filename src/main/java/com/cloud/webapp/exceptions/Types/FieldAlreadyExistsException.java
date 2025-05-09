package com.cloud.webapp.exceptions.Types;

import com.cloud.webapp.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FieldAlreadyExistsException extends BaseException {
    public FieldAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue),
                resourceName, fieldName, fieldValue);
    }
}