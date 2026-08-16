package com.admissionconsultancy.exception;

import com.admissionconsultancy.constants.AppCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
class AppException extends RuntimeException {
    private final AppCode appCode;
    private final HttpStatus httpStatus;

    public AppException(AppCode appCode, HttpStatus httpStatus) {
        super(appCode.getMessage());
        this.appCode = appCode;
        this.httpStatus = httpStatus;
    }

    public AppException(AppCode appCode, String customMessage, HttpStatus httpStatus) {
        super(customMessage);
        this.appCode = appCode;
        this.httpStatus = httpStatus;
    }
}