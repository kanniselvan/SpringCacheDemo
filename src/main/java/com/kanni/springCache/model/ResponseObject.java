package com.kanni.springCache.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseObject<T> {

    private int statusCode;

    private String message;

    private T response;
}
