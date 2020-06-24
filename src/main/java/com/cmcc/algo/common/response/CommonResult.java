package com.cmcc.algo.common.response;

import com.github.pagehelper.Page;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

/**
 * 通用返回对象
 *
 */
public class CommonResult<T> implements Serializable {
    private static final long serialVersionUID = 7459014710469828196L;

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 返回状态码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回内容
     */
    private T data;
    /**
     * 分页信息
     */
    private PageInfo pageInfo;
    /**
     * 其他信息
     */


    private Map<String, Object> ext;

    public CommonResult() {
        this.success = true;
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
    }

    public CommonResult(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    protected CommonResult(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResult(boolean success, int code, String message, T data, Map<String, Object> ext) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.ext = ext;
    }

    public CommonResult(boolean success, int code, String message, T data, PageInfo pageInfo) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public CommonResult(boolean success, int code, String message, T data, Map<String, Object> ext, PageInfo pageInfo) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.pageInfo = pageInfo;
        this.ext = ext;
        this.data = data;
    }

    public CommonResult(boolean success, int code, String message, T data, long total, long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public CommonResult(boolean success, int code, String message, T data, Map<String, Object> ext, long total,
                        long pageNum, long step) {
        this.success = success;
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        this.code = code;
        this.message = message;
        this.data = data;
        this.ext = ext;
        this.pageInfo = pageInfo;
    }

    /**
     * 快速返回成功
     */
    public static <T> CommonResult success() {
        return new CommonResult(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> CommonResult success(T data) {
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult success(String message, T data) {
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResult<T> success(String message, T data, Map<String, Object> ext) {
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), message, data, ext);
    }

    public static <T> CommonResult<T> success(T data, long total, long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, pageInfo);
    }

    public static <T> CommonResult<T> success(T data, Page page) {
        long total = page.getTotal();
        int pageNum = page.getPageNum();
        int step = page.getPageSize();
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, pageInfo);
    }

    public static <T> CommonResult<T> success(T data, Map<String, Object> ext, long total, long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, ext,
                pageInfo);
    }

    public static <T> CommonResult<T> success(String message, T data, long total, long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), message, data, pageInfo);
    }

    public static <T> CommonResult<T> success(String message, T data, Map<String, Object> ext, long total,
                                              long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(true, ResultCode.SUCCESS.getCode(), message, data, ext, pageInfo);
    }

    /**
     * 快速返回失败状态
     */
    public static <T> CommonResult<T> fail() {
        return new CommonResult<T>(false, ResultCode.SYSTEM_ERROR.getCode(), ResultCode.SYSTEM_ERROR.getMessage());
    }

    public static <T> CommonResult<T> fail(T data) {
        return new CommonResult<T>(false, ResultCode.SYSTEM_ERROR.getCode(), ResultCode.SYSTEM_ERROR.getMessage(),
                data);
    }

    public static <T> CommonResult<T> fail(String message, T data) {
        return new CommonResult<T>(false, ResultCode.SYSTEM_ERROR.getCode(), message, data);
    }

    public static <T> CommonResult<T> fail(String message, T data, Map<String, Object> ext) {
        return new CommonResult<T>(false, ResultCode.SYSTEM_ERROR.getCode(), message, data, ext);
    }

    public static <T> CommonResult<T> fail(ResultCode resultCode) {
        return new CommonResult<T>(false, resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> CommonResult<T> fail(ResultCode resultCode, T data) {
        return new CommonResult<T>(false, resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> CommonResult<T> fail(ResultCode resultCode, String message, T data) {
        return new CommonResult<T>(false, resultCode.getCode(), message, data);
    }

    public static <T> CommonResult<T> fail(ResultCode resultCode, String message, T data, Map<String, Object> ext) {
        return new CommonResult<T>(false, resultCode.getCode(), message, data, ext);
    }

    /**
     * 快速返回自定义状态码
     */
    public static <T> CommonResult<T> result(boolean success, int code, String message) {
        return new CommonResult<T>(success, code, message);
    }

    public static <T> CommonResult<T> result(boolean success, int code, String message, T data) {
        return new CommonResult<T>(success, code, message, data);
    }

    public static <T> CommonResult<T> result(boolean success, int code, String message, T data,
                                             Map<String, Object> ext) {
        return new CommonResult<T>(success, code, message, data, ext);
    }

    public static <T> CommonResult<T> result(boolean success, int code, String message, T data, long total,
                                             long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(success, code, message, data, pageInfo);
    }

    public static <T> CommonResult<T> result(boolean success, int code, String message, T data,
                                             Map<String, Object> ext, long total, long pageNum, long step) {
        PageInfo pageInfo = new PageInfo(total, pageNum, step);
        return new CommonResult<T>(success, code, message, data, ext, pageInfo);
    }

    /**
     * 快速返回HTTP状态
     */
    public static <T> CommonResult<T> httpStatus(boolean success, HttpStatus httpStatus, String message) {
        return result(success, httpStatus.value(), message);
    }

    public static <T> CommonResult<T> httpStatus(boolean success, HttpStatus httpStatus, String message, T data) {
        return result(success, httpStatus.value(), message, data);
    }

    public static <T> CommonResult<T> httpStatus(boolean success, HttpStatus httpStatus, String message, T data,
                                                 Map<String, Object> ext) {
        return result(success, httpStatus.value(), message, data, ext);
    }

    public static <T> CommonResult<T> httpStatus(boolean success, HttpStatus httpStatus, String message, T data,
                                                 long total, long pageNum, long step) {
        return result(success, httpStatus.value(), message, data, total, pageNum, step);
    }

    public static <T> CommonResult<T> httpStatus(boolean success, HttpStatus httpStatus, String message, T data,
                                                 Map<String, Object> ext, long total, long pageNum, long step) {
        // PageUtil pageUtil = new PageUtil(total, pageNum, step);
        return result(success, httpStatus.value(), message, data, ext, total, pageNum, step);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }
}
