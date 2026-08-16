package com.admissionconsultancy.exception;

import com.admissionconsultancy.constants.AppCode;
import org.springframework.http.HttpStatus;

public final class UnauthorizedException extends AppException {
    public UnauthorizedException(String customMessage) {
        super(AppCode.UNAUTHORIZED, customMessage, HttpStatus.UNAUTHORIZED);
    }
}
