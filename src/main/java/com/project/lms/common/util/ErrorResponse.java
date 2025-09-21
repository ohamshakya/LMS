package com.project.lms.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;


public class ErrorResponse {
    public static ResponseEntity<ResponseWrapper<String>> buildErrorResponse(String message, HttpStatus status){
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(null,message,status.value(),false);
        return ResponseEntity.status(status).body(responseWrapper);
    }

    public static<T> ResponseEntity<ResponseWrapper<T>> buildErrorResponse(T data,String message,HttpStatus status){
        ResponseWrapper<T> responseWrapper = new ResponseWrapper<>(data,message,status.value(),false);
        return ResponseEntity.status(status).body(responseWrapper);
    }

    public static ResponseEntity<ResponseWrapper<Map<String,String>>> buildErrorResponseMap(Map<String,String> errorsMessage,HttpStatus status){
        ResponseWrapper<Map<String,String>> errors = new ResponseWrapper<>();
        errors.setData(null);
        errors.setMessage("Validation failed");
        errors.setData(errorsMessage);
        return ResponseEntity.status(status).body(errors);
    }
}
