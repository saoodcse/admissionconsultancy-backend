package com.admissionconsultancy.exception;

import com.admissionconsultancy.constants.AppCode;
import org.springframework.http.HttpStatus;

public final class DuplicateResourceException extends AppException {
    public DuplicateResourceException(AppCode appCode) {
        super(appCode, HttpStatus.CONFLICT);
    }
}
