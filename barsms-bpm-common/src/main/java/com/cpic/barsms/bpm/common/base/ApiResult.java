package com.cpic.barsms.bpm.common.base;

import com.cpic.barsms.bpm.common.enums.ResponseCodeEnum;
import com.cpic.barsms.bpm.common.utils.ThreadTraceUtils;
import lombok.Data;

@Data
public class ApiResult<T> {

    private T data;
    private boolean isSuccess = true;
    private Integer code = ResponseCodeEnum.SUCESS.getCode();
    private String message = ResponseCodeEnum.SUCESS.getMessage();
    private String remarks;
    private String threadTraceId;

    public ApiResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public static ApiResult ok() {
        ApiResult r = new ApiResult();
        r.setSuccess(true);
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult ok(Object data) {
        ApiResult r = new ApiResult();
        r.setSuccess(true);
        r.setData(data);
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult ok(ResponseCodeEnum responseCodeEnum) {
        ApiResult r = new ApiResult();
        r.setSuccess(true);
        r.setCode(responseCodeEnum.getCode());
        r.setMessage(responseCodeEnum.getMessage());
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult ok(String message) {
        ApiResult r = new ApiResult();
        r.setSuccess(true);
        r.setMessage(message);
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult error() {
        ApiResult r = new ApiResult();
        r.setSuccess(false);
        r.setCode(ResponseCodeEnum.ERROR.getCode());
        r.setMessage(ResponseCodeEnum.ERROR.getMessage());
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult error(Integer code, String message) {
        ApiResult r = new ApiResult();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

    public static ApiResult error(ResponseCodeEnum responseCodeEnum) {
        ApiResult r = new ApiResult();
        r.setSuccess(false);
        r.setCode(responseCodeEnum.getCode());
        r.setMessage(responseCodeEnum.getMessage());
        r.setThreadTraceId(ThreadTraceUtils.getThreadTraceId());
        return r;
    }

}
