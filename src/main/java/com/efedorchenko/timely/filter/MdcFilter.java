package com.efedorchenko.timely.filter;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
public class MdcFilter implements WebFilter {

    public static final String RQUID = "RqUID";

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        @Nullable String rquid = exchange.getRequest().getHeaders().getFirst(RQUID);
        ServerHttpResponse response = exchange.getResponse();

        if (rquid == null || rquid.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return response.setComplete();
        }

        response.getHeaders().set(RQUID, rquid);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(RQUID, rquid));
    }
}
