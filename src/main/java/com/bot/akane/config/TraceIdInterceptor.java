package com.bot.akane.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bot.akane.util.TraceIdUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 链路追踪拦截器
 * 为每个请求自动生成和管理追踪 ID
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取追踪 ID，如果没有则生成新的
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdUtil.generateTraceId();
        }
        TraceIdUtil.setTraceId(traceId);
        
        // 将追踪 ID 添加到响应头
        response.setHeader(TRACE_ID_HEADER, traceId);
        
        log.debug("Request started with traceId: {}, method: {}, uri: {}", 
                  traceId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request failed with exception", ex);
        }
        // 清空 MDC
        TraceIdUtil.clear();
    }
}
