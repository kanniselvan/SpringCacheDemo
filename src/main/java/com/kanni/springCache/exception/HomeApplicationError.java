package com.kanni.springCache.exception;

import lombok.Data;

@Data
public class HomeApplicationError extends RuntimeException{

    private String  errorMessage;

    public HomeApplicationError(String message,Throwable throwable){
        super(message,throwable);
        this.errorMessage=message;
    }
}
