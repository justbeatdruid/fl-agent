package com.cmcc.algo.common;

/**
 * 枚举了一些常用API操作码
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "请求成功"),
    PARAMETER_CHECK_ERROR(400, "参数检验失败"),
    UNAUTHORIZED(401, "用户未登录或登录状态超时失效"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "请求资源不存在"),
    REQUEST_TIMEOUT(408,"请求超时"),
    REQUEST_ERROR(409,"请求错误"),
    SYSTEM_ERROR(500, "服务器错误"),
    RESPONSE_ERROR(501, "服务器无法识别请求"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    NOT_SUPPORT(505, "不支持的HTTP协议版本"),
    AUTH_VALID_ERROR(701, "用户权限不足");

    final private Integer code;
    final private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return code.toString() + " = " + message;
    }

    public static ResultCode getByCode(Integer code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }

        return null;
    }
}
