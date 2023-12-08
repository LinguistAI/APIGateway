package app.linguistai.gateway.exception;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class ErrorFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange) // TODO when there is no error this is executed
                .then(Mono.defer(() -> {
                    if (exchange.getResponse().getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        // Customize the error response
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                        return exchange.getResponse().writeWith(Mono.just(
                                exchange.getResponse().bufferFactory().wrap("Custom Error Response".getBytes())
                        ));
                    }
                    return Mono.empty();
                }));
    }
}

