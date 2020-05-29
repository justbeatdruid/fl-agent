package com.cmcc.algo.common;

public class APIException extends RuntimeException{
    private ResultCode resultCode;
    private String message;
    private Object data;

    public ResultCode getResultCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public APIException(String message) {
        this.resultCode = ResultCode.SYSTEM_ERROR;
        this.message = message;
        this.data = null;
    }

    public APIException(ResultCode resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = null;
    }

    public APIException(ResultCode resultCode, String message, Object data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }
}
