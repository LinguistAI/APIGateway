package app.linguistai.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RouterValidator {


    @Value("${spring.base.prefix}")
    private String BASE_PREFIX;

    public static List<Pattern> openApiEndpointPatterns;

    private void initializeOpenApiEndpoints() {
        List<String> endpointPatterns = List.of(

                BASE_PREFIX + "/v3/api-docs",
                BASE_PREFIX + "/v3/api-docs/.*",
                BASE_PREFIX + "/swagger-ui/.*",
                BASE_PREFIX + "/auth/hello",
                BASE_PREFIX + "/auth/login",
                BASE_PREFIX + "/auth/register",
                BASE_PREFIX + "/auth/request-reset",
                BASE_PREFIX + "/auth/test-reset",
                BASE_PREFIX + "/auth/validate-reset",
                BASE_PREFIX + "/auth/reset-password",
                BASE_PREFIX + "/ml/swagger",
                BASE_PREFIX + "/ml/redoc",
                BASE_PREFIX + "/ml/redoc/.*",
                BASE_PREFIX + "/ml/swagger/.*",
                BASE_PREFIX + "/ml/static/.*",
                BASE_PREFIX + "/ml/static",
                BASE_PREFIX + "/ml/ping.*",
                BASE_PREFIX + "/ml/admin.*",
                BASE_PREFIX + "/ping.*",
                BASE_PREFIX + "/dictionary/ping.*",
                BASE_PREFIX + "/auth/reset-password"

        );

        // Convert endpoint patterns to regex patterns
        openApiEndpointPatterns = endpointPatterns.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        initializeOpenApiEndpoints();
    }

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpointPatterns.stream()
                    .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());


}
