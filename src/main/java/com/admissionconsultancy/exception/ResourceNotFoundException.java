package com.admissionconsultancy.exception;

import com.admissionconsultancy.constants.AppCode;
import org.springframework.http.HttpStatus;

public final class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(AppCode appCode) {
        super(appCode, HttpStatus.NOT_FOUND);
    }
    public ResourceNotFoundException(String customMessage) {
        super(AppCode.RESOURCE_NOT_FOUND, customMessage, HttpStatus.NOT_FOUND);
    }
}
