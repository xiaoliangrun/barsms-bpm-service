package com.cpic.barsms.bpm.common.exception;

import com.cpic.barsms.bpm.common.enums.ResponseCodeEnum;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final Integer code;

    public BizException(String message) {
        super(message);
        this.code = ResponseCodeEnum.ERROR.getCode();
    }

    public BizException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMessage());
        this.code = responseCodeEnum.getCode();
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
