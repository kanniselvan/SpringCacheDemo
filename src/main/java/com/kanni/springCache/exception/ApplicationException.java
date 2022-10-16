package com.kanni.springCache.exception;

import com.kanni.springCache.model.ResponseObject;
import com.kanni.springCache.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApplicationException {

    @ExceptionHandler(value=JWTException.class)
    public ResponseObject getJwtException(JWTException exception){
        ResponseObject responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        responseObject.setMessage(exception.getMessage());
        return responseObject;
    }


    @ExceptionHandler(value=HomeApplicationError.class)
    public ResponseObject getApplicationError(HomeApplicationError homeApplicationError, WebRequest request){
        return ResponseUtils.errorMessage(homeApplicationError.getErrorMessage());
    }

    @ExceptionHandler(value= AccessDeniedException.class)
    public ResponseObject accessDeniedException(AccessDeniedException exception, WebRequest request){
        ResponseObject responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.FORBIDDEN.value());
        responseObject.setMessage(exception.getMessage());
        return responseObject;
    }

    @ExceptionHandler(value=Exception.class)
    public ResponseObject getUnknownError(Exception exception, WebRequest request){
        return ResponseUtils.errorMessage(exception.getMessage());
    }


}
