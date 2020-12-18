package com.cl.security.common.result;

import lombok.Data;

/**
* 通用返回对象
* @author chenlong
* @date 2020/12/8
*/
@Data
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;
    protected CommonResult() {

    }
    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 请求成功返回结果
     * @param data data
     * @param <T> type
     * @return success
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 自定义消息的请求成功返回结果
     * @param data data
     * @param message message
     * @param <T> type
     * @return success
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 为不同的请求失败的方法定义的通用方法
     * @param resultCodeEnum 错误码
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed(ResultCodeEnum resultCodeEnum) {
        return new CommonResult<T>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), null);
    }

    /**
     * 自定义消息的返回结果
     * @param message message
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 请求失败的返回结果
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCodeEnum.FAILED);
    }

    /**
     * 参数验证失败的返回结果
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 自定义消息的参数验证失败的返回结果
     * @param message message
     * @param <T> type
     * @return failed
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     * @param data data
     * @param <T> data
     * @return 未登录
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     * @param data data
     * @param <T> type
     * @return 未授权
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(), data);
    }



}
