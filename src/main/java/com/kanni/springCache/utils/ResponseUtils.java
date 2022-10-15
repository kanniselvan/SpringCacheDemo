package com.kanni.springCache.utils;

import com.kanni.springCache.model.ResponseObject;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@UtilityClass
public class ResponseUtils {

    public <T,V> ResponseObject<V> getResult(T type, V result){
        ResponseObject<V> responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.OK.value());
        responseObject.setResponse(result);
        return responseObject;
    }

    public ResponseObject noDataFound(){
        ResponseObject responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.NO_CONTENT.value());
        responseObject.setMessage("No Data Found!!!!");
        return responseObject;
    }

    public static ResponseObject successMessage(String message) {
        ResponseObject responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.OK.value());
        responseObject.setMessage(message);
        return responseObject;
    }

    public static ResponseObject errorMessage(String message) {
        ResponseObject responseObject=new ResponseObject<>();
        responseObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseObject.setMessage(message);
        return responseObject;
    }
}
