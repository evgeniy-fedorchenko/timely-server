package com.efedorchenko.timely.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class RequestUidFilter extends OncePerRequestFilter implements Ordered {

    public static final String RQUID = "RqUID";
    private static final Pattern RQUID_PATTERN = Pattern.compile("^[a-zA-Z\\d-]{20,40}$");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String rquid = request.getHeader(RQUID);

        if (rquid == null || rquid.isEmpty() || !RQUID_PATTERN.matcher(rquid).matches()) {
            badRequest(response);
            return;
        }

        try {
            MDC.put(RQUID, rquid);
            response.setHeader(RQUID, rquid);
            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }

    private void badRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseBody = RQUID + " is required and must be matches " + RQUID_PATTERN.pattern();
        response.getWriter().write(responseBody);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
