package app.linguistai.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
        "/api/v1/v3/api-docs/**",
        "/api/v1/swagger-ui/**",
        "/api/v1/auth/hello",
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/request-reset",
        "/api/v1/auth/test-reset",
        "/api/v1/auth/validate-reset",
        "/api/v1/auth/reset-password"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().equals(uri));

}
