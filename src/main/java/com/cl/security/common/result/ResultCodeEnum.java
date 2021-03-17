package com.cl.security.common.result;

/**
*
* @author chenlong
* @date 2020/12/8
*/
public enum ResultCodeEnum {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(405, "参数校验失败"),
    UNAUTHORIZED(401, "暂未登录或token过期"),
    FORBIDDEN(403, "没有权限");
    // 状态码
    private long code;
    // 结果信息
    private String message;
    private ResultCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
