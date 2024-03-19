package com.fawry.orderService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(value = ResponseStatusException.class)
    ResponseEntity<ErrorResponse> handleRestTemplateException(ResponseStatusException ex){
        return new ResponseEntity<>(new ErrorResponse(ex.getStatusCode().value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> handleInvalidArgumentException(MethodArgumentNotValidException ex){
        Map<String ,String>errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errorMap,HttpStatus.BAD_REQUEST);
    }
}
