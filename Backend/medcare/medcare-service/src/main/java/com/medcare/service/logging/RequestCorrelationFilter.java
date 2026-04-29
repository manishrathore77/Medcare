package com.medcare.service.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Assigns a per-request correlation id for log tracing.
 * <p>
 * Uses the {@code X-Request-Id} header when present; otherwise generates a UUID.
 * The value is stored in {@link MDC} under {@value #MDC_REQUEST_ID} so log patterns can
 * include {@code %X{requestId}}.
 * </p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {

    /** MDC key used in {@code logging.pattern} as {@code %X{requestId}}. */
    public static final String MDC_REQUEST_ID = "requestId";

    /** Incoming header that carries a client-supplied correlation id (optional). */
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String incoming = request.getHeader(HEADER_REQUEST_ID);
        String id = StringUtils.hasText(incoming) ? incoming.trim() : UUID.randomUUID().toString();
        MDC.put(MDC_REQUEST_ID, id);
        response.setHeader(HEADER_REQUEST_ID, id);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID);
        }
    }
}
