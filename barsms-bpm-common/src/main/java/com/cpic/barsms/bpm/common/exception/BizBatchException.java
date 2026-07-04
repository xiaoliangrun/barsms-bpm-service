package com.cpic.barsms.bpm.common.exception;

import com.cpic.barsms.bpm.common.enums.ResponseCodeEnum;
import lombok.Getter;

@Getter
public class BizBatchException extends RuntimeException {
    private final Integer code;

    public BizBatchException(String message) {
        super(message);
        this.code = ResponseCodeEnum.ERROR.getCode();
    }

    public BizBatchException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMessage());
        this.code = responseCodeEnum.getCode();
    }

    public BizBatchException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
