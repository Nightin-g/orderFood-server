package com.example.orderfood.demos.web.handler;

import com.example.orderfood.demos.web.exception.BusinessException;
import com.example.orderfood.demos.web.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R handleBusinessException(BusinessException e) {
        logger.error("业务异常: {}", e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error("系统异常", e);
        return R.error(500, "系统异常，请稍后重试");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常", e);
        return R.error(500, e.getMessage() != null ? e.getMessage() : "运行时异常，请稍后重试");
    }
}
