package com.efedorchenko.timely.filter;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.efedorchenko.timely.configuration.ApplicationProperties.RQUID;
import static com.efedorchenko.timely.configuration.ApplicationProperties.RQUID_PATTERN;

@Slf4j
@Component
public class RequestUidFilter extends OncePerRequestFilter implements Ordered {

    private static final RequestMatcher BASE_MATCHER =
            new AntPathRequestMatcher(ApplicationProperties.BASE_PATH + "/**");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!BASE_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String rquid = request.getHeader(RQUID);

        if (rquid == null || rquid.isEmpty() || !RQUID_PATTERN.matcher(rquid).matches()) {
            badRequest(response);
            return;
        }

        try {
            MDC.put(RQUID, rquid);
            response.setHeader(RQUID, rquid);

            if (log.isDebugEnabled() && !this.isAsyncStarted(request) && !this.isAsyncDispatch(request)) {
                log.debug("Start processing: uri [{}], ip: [{}]", request.getRequestURI(), request.getRemoteAddr());
            }
            filterChain.doFilter(request, response);

            if (log.isDebugEnabled() && this.isAsyncStarted(request)) {
                log.trace("Async processing: uri [{}], ip: [{}]", request.getRequestURI(), request.getRemoteAddr());
            }

            if (log.isDebugEnabled() && !this.isAsyncStarted(request)) {
                log.trace("Finish processing: uri [{}], ip: [{}]", request.getRequestURI(), request.getRemoteAddr());
            }
        } catch (Throwable t) {
            log.error("Error processing: uri [{}], ip: [{}]", request.getRequestURI(), request.getRemoteAddr(), t);
        } finally {
            MDC.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void badRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseBody = RQUID + " is required and must be matches " + RQUID_PATTERN.pattern();
        response.getWriter().write(responseBody);
    }
}
