package com.disk.handler;

import com.disk.constant.MessageConstant;
import com.disk.exception.BaseException;
import com.disk.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        String msg = ex.getMessage();
        log.error("异常信息:{}", msg);
        return Result.error(msg);
    }
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.info(ex.getMessage());
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
