package com.project.lms.common.advice;

import com.project.lms.common.exception.*;
import com.project.lms.common.util.ErrorResponse;
import com.project.lms.common.util.ResponseWrapper;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.xml.bind.ValidationException;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ResponseWrapper<String>> validationException(ValidationException e) {
        return ErrorResponse.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleMethodValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ErrorResponse.buildErrorResponseMap(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<ResponseWrapper<String>> handleAlreadyExistsException(AlreadyExistsException e){
        return ErrorResponse.buildErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ResponseWrapper<String>> handleResourceNotFoundException(ResourceNotFoundException ex){
        return ErrorResponse.buildErrorResponse(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BorrowException.class})
    public ResponseEntity<ResponseWrapper<String>> handleBorrowException(BorrowException e){
        return ErrorResponse.buildErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<ResponseWrapper<String >> handleTokenExpiredException(TokenExpiredException e){
        return ErrorResponse.buildErrorResponse(e.getMessage(),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<ResponseWrapper<String>> handleInvalidTokenException(InvalidTokenException e){
        return ErrorResponse.buildErrorResponse(e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<ResponseWrapper<String>> handleSignatureException(SignatureException e){
        return ErrorResponse.buildErrorResponse(e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "forbidden");
        response.put("message", "You do not have permission to access this resource.");
        response.put("status", 403);
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<ResponseWrapper<String>> handleNoHandlerException(NoHandlerFoundException e){
        String message = "No handler found for " + e.getHttpMethod() + " " + e.getRequestURL();
        return ErrorResponse.buildErrorResponse(message, HttpStatus.NOT_FOUND);
    }

}
