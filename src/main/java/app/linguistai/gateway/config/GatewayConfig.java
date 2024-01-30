package app.linguistai.gateway.config;

import java.util.HashMap;

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
    
    @Value("${spring.uri.ml}")
    private String URI_ML;

    public static final String DICTINOARY_SERVICE_ID = "dictionary-service";
    public static final String USER_SERVICE_ID = "user-service";
    public static final String ML_ID = "ml-service";

    public static final HashMap<String, String> ROUTES = new HashMap<>();

    static {
        ROUTES.put(DICTINOARY_SERVICE_ID, "/api/v1/dictionary/**");
        ROUTES.put(ML_ID, "/api/v1/ml/**");
        ROUTES.put(USER_SERVICE_ID, "/api/v1/**");
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(DICTINOARY_SERVICE_ID, r -> r.path(ROUTES.get(DICTINOARY_SERVICE_ID))
                        .filters(f -> f.filter(filter))
                        .uri(URI_DICTIONARY))
                .route(ML_ID, r -> r.path(ROUTES.get(ML_ID))
                        .filters(f -> f.filter(filter))
                        .uri(URI_ML))
                .route(USER_SERVICE_ID, r -> r.path(ROUTES.get(USER_SERVICE_ID))
                        .filters(f -> f.filter(filter))
                        .uri(URI_USER))                
                .build();
    }

}