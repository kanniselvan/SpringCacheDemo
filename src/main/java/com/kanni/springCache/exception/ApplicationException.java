package com.kanni.springCache.exception;

import com.kanni.springCache.model.ResponseObject;
import com.kanni.springCache.utils.ResponseUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApplicationException {

    @ExceptionHandler(value=HomeApplicationError.class)
    public ResponseObject getApplicationError(HomeApplicationError homeApplicationError, WebRequest request){
        return ResponseUtils.errorMessage(homeApplicationError.getErrorMessage());
    }

    @ExceptionHandler(value=Exception.class)
    public ResponseObject getUnknownError(Exception exception, WebRequest request){
        return ResponseUtils.errorMessage(exception.getMessage());
    }

}
