package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获重复注册exception
     * @param exception
     * @return
     */
    @ExceptionHandler
    public Result SQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception){
        log.error("异常错误之重复输入：{}", exception.getMessage());
        String[] s = exception.getMessage().split(" ");
        System.out.println(Arrays.toString(s));
        return Result.error("账户名为{"+s[2]+"}"+"的用户已存在,无法重复注册");
    }

}
