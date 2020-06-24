package com.cmcc.algo.common.response;

import java.io.Serializable;

/**
 * 通用返回对象
 *
 */
public class CommonResultMessage<T> extends CommonResult<T> implements Serializable {
    private static final long serialVersionUID = 7459014710469828496L;

    public CommonResultMessage(boolean b, Integer code, String message) {
        super(b, code, message);
    }

    public CommonResultMessage(boolean b, Integer code, String message,T data) {
        super(b, code, message,data);
    }

    public static <T> CommonResultMessage<T> fail(ResultCode resultCode, String message) {
        return new CommonResultMessage<T>(false, resultCode.getCode(), message);
    }

    public static <T> CommonResultMessage<T> fail(ResultCode resultCode, String message, T data) {
        return new CommonResultMessage<T>(false, resultCode.getCode(), message,data);
    }
}
