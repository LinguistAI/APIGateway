package app.linguistai.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    JWTFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/v1/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://localhost:8081"))

                // .route("auth-service", r -> r.path("/auth/**")
                //         .filters(f -> f.filter(filter))
                //         .uri("lb://auth-service"))
                .build();
    }

}