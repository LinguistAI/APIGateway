package app.linguistai.gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.linguistai.gateway.utils.Logger;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class ExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        System.out.println("aloooooo");
        Logger.error(ex.getMessage());
        ex.printStackTrace();

        if (ex instanceof java.net.ConnectException || ex instanceof org.springframework.web.client.ResourceAccessException) {
            return handleMicroserviceNotWorking(exchange, ex);
        } else if (ex instanceof ResponseStatusException) {
            return handleResponseStatusException(exchange, (ResponseStatusException) ex);
        } else if (ex instanceof JWTException) {
            return handleJWTException(exchange, (JWTException) ex);
        } else {
            return Mono.error(ex);
        }        
    }

    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        exchange.getResponse().setStatusCode(ex.getStatusCode());
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> handleMicroserviceNotWorking(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);

        try {
            String jsonResponse = objectMapper.writeValueAsString(Response.create("The requested service is currently unavailable. Please try again later.", status));
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(jsonResponse.getBytes())));
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return Mono.error(new Exception("Something went wrong!"));
        }
    }

    private Mono<Void> handleJWTException(ServerWebExchange exchange, JWTException ex) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(ex.getStatus());

        try {
            String jsonResponse = objectMapper.writeValueAsString(Response.create(ex.getMessage(), ex.getStatus()));
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(jsonResponse.getBytes())));
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
            return Mono.error(new Exception("Something went wrong!"));
        }
    }
}

