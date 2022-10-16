package com.kanni.springCache.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class HomeApplicationError extends RuntimeException{

    private String  errorMessage;
    private HttpStatus httpStatus;

    public HomeApplicationError(String message){
        super(message);
        this.errorMessage=message;
    }

    public HomeApplicationError(String message, HttpStatus internalServerError){
        super(message);
        this.errorMessage=message;
        this.httpStatus=internalServerError;
    }

}
