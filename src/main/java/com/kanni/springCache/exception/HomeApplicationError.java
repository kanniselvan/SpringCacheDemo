package com.kanni.springCache.exception;

import lombok.Data;

@Data
public class HomeApplicationError extends RuntimeException{

    private String  errorMessage;

    public HomeApplicationError(String message){
        super(message);
        this.errorMessage=message;
    }
}
