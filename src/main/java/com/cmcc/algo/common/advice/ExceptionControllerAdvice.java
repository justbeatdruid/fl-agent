package com.cmcc.algo.common.advice;

import com.cmcc.algo.common.exception.APIException;
import com.cmcc.algo.common.response.CommonResult;
import com.cmcc.algo.common.response.ResultCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(APIException.class)
    public CommonResult handleAPIException(APIException e){
        return CommonResult.fail(e.getResultCode(),e.getMessage(),e.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return CommonResult.fail(ResultCode.NOT_FOUND, "参数校验失败", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult handleException(Exception e){
        return CommonResult.fail(ResultCode.SYSTEM_ERROR, e.getMessage());
    }
}
