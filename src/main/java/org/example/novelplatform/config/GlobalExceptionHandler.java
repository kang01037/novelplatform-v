package org.example.novelplatform.config;

import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseMessage<Void>> handleServiceException(ServiceException e) {
        return ResponseEntity.status(e.getCode()).body(ResponseMessage.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<Void>> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMessage.error("系统错误，请稍后重试"));
    }
}
