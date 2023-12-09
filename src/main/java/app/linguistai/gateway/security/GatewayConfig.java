package app.linguistai.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    JWTFilter filter;

    @Value("${spring.uri.user}")
    private String URI_USER;
    
    @Value("${spring.uri.dictionary}")
    private String URI_DICTIONARY;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("dictionary-service", r -> r.path("/api/v1/dict/**")
                        .filters(f -> f.filter(filter))
                        .uri(URI_DICTIONARY))
                .route("user-service", r -> r.path("/api/v1/**")
                        .filters(f -> f.filter(filter))
                        .uri(URI_USER))                
                .build();
    }

}