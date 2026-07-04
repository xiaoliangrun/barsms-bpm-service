package com.cpic.barsms.bpm.common.config;

import com.cpic.barsms.bpm.common.base.ApiResult;
import com.cpic.barsms.bpm.common.enums.ResponseCodeEnum;
import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 全局异常处理器
 * @Date 2026/6/28
 * @Created by xiaoliang.ruan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizBatchException.class)
    public ApiResult<Void> handleBizBatchException(BizBatchException e) {
        log.warn("批量跑批业务异常: {}", e.getMessage());
        return ApiResult.error(ResponseCodeEnum.ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(BizException.class)
    public ApiResult<Void> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResult.error(ResponseCodeEnum.ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("参数校验失败");
        return ApiResult.error(ResponseCodeEnum.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ApiResult.error(ResponseCodeEnum.ERROR);
    }
}
