package cn.xm.xy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        log.info(">>> {} {} {}", request.getMethod(), request.getRequestURI(),
                request.getQueryString() != null ? "?" + request.getQueryString() : "");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long start = (long) request.getAttribute(START_TIME);
        long cost = System.currentTimeMillis() - start;
        if (ex != null) {
            log.error("<<< {} {} {}ms status={} error={}", request.getMethod(), request.getRequestURI(), cost, response.getStatus(), ex.getMessage());
        } else {
            log.info("<<< {} {} {}ms status={}", request.getMethod(), request.getRequestURI(), cost, response.getStatus());
        }
    }
}
