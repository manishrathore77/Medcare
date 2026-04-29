package com.medcare.service.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Correlates REST and service-layer execution with {@link MDC} {@code requestId} for tracing.
 * <p>
 * API calls are logged at {@code INFO} with HTTP method, URI, handler, and duration.
 * Service calls log at {@code INFO} when a request correlation id is present, otherwise
 * at {@code DEBUG} for non-web use (e.g. scheduled jobs).
 * </p>
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class ApplicationLoggingAspect {

    @Around("execution(* com.medcare.service.service.impl..*(..))")
    public Object logServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution("SERVICE", joinPoint, false);
    }

    @Around("execution(* com.medcare.service.controller..*(..))")
    public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution("API", joinPoint, true);
    }

    private Object logExecution(String layer, ProceedingJoinPoint joinPoint, boolean httpContext) throws Throwable {
        String trace = MDC.get(RequestCorrelationFilter.MDC_REQUEST_ID);
        String signature = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long ms = (System.nanoTime() - start) / 1_000_000L;
            if (httpContext) {
                logApiSuccess(trace, signature, ms);
            } else {
                logServiceSuccess(trace, signature, ms);
            }
            return result;
        } catch (Throwable ex) {
            long ms = (System.nanoTime() - start) / 1_000_000L;
            log.warn("[trace={}] {} FAILED {} after {} ms: {}", traceIdOrDash(trace), layer, signature, ms, ex.toString());
            throw ex;
        }
    }

    private void logApiSuccess(String trace, String signature, long ms) {
        String tid = traceIdOrDash(trace);
        HttpServletRequest req = currentRequest();
        if (req != null) {
            String qs = req.getQueryString();
            String path = qs != null ? req.getRequestURI() + "?" + qs : req.getRequestURI();
            log.info("[trace={}] API {} {} | handler={} | {} ms", tid, req.getMethod(), path, signature, ms);
        } else {
            log.info("[trace={}] API (no request context) | handler={} | {} ms", tid, signature, ms);
        }
    }

    private void logServiceSuccess(String trace, String signature, long ms) {
        // Detailed business logs live inside *ServiceImpl; keep AOP timing at DEBUG to avoid duplicate INFO lines.
        if (log.isDebugEnabled()) {
            log.debug("[trace={}] SERVICE {} | {} ms", traceIdOrDash(trace), signature, ms);
        }
    }

    private static String traceIdOrDash(String trace) {
        return trace != null ? trace : "-";
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
