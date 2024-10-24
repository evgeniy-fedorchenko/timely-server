package com.efedorchenko.timely.filter;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Component
public class RequestUidFilter implements WebFilter {

    public static final String RQUID = "RqUID";
    private static final Pattern RQUID_PATTERN = Pattern.compile("^[a-zA-Z\\d]{20,40}$");

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String rquid = exchange.getRequest().getHeaders().getFirst(RQUID);
        ServerHttpResponse response = exchange.getResponse();

        if (rquid == null || rquid.isEmpty() || RQUID_PATTERN.matcher(rquid).matches()) {
            return badRequest(response);
        }
        response.getHeaders().set(RQUID, rquid);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(RQUID, rquid));
    }

    private Mono<Void> badRequest(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        String responseBody = RQUID + " is required and must be matches " + RQUID_PATTERN.pattern();
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
