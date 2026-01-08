package com.eys.common.exception;

import com.eys.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑校验失败时抛出
 *
 * @author EYS
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 使用错误码枚举构造
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 使用错误码枚举 + 自定义消息构造
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 使用自定义错误码和消息构造
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用默认失败码 + 自定义消息构造
     */
    public BizException(String message) {
        super(message);
        this.code = ResultCode.FAILURE.getCode();
        this.message = message;
    }
}
