package com.ecommerce.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class ApiException  extends RuntimeException{

    public ApiException(String message){
        super(message);
    }

    public ApiException(String message, Throwable cause){
        super(message, cause);
    }
}
