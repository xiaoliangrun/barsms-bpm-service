package com.cpic.barsms.bpm.common.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ThreadTraceUtils {

    private static String KEY = "ThreadTraceId";

    public static String setThreadTraceId() {
        try {
            String threadTraceId = doSetThreadTraceId();
            return threadTraceId;
        } catch (Throwable t) {
            return "";
        }
    }

    private static String doSetThreadTraceId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null || !(requestAttributes instanceof ServletRequestAttributes)) {
            return "";
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        String random6 = RandomUtils.getRandom10();
        request.setAttribute(KEY, random6);
        return random6;
    }

    public static String getThreadTraceId() {
        try {
            String threadTraceId = doGetThreadTraceId();
            return threadTraceId;
        } catch (Throwable t) {
            return "";
        }
    }

    private static String doGetThreadTraceId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null || !(requestAttributes instanceof ServletRequestAttributes)) {
            return "";
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        Object threadTraceId = request.getAttribute(KEY);
        if (threadTraceId == null || !(threadTraceId instanceof String)) {
            return "";
        }
        return (String) threadTraceId;
    }
}
