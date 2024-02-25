package app.linguistai.gateway.config;

import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

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

    @Value("${spring.base.prefix}")
    private String BASE_PREFIX;

    public static final String DICTINOARY_SERVICE_ID = "dictionary-service";
    public static final String USER_SERVICE_ID = "user-service";
    public static final String ML_ID = "ml-service";

    public static final HashMap<String, String> ROUTES = new HashMap<>();

    @PostConstruct
    public void init() {
        ROUTES.put(DICTINOARY_SERVICE_ID, BASE_PREFIX + "/dictionary/**");
        ROUTES.put(ML_ID, BASE_PREFIX + "/ml/**");
        ROUTES.put(USER_SERVICE_ID, BASE_PREFIX + "/**");

        System.out.println("Routes initialized: " + ROUTES);
    }


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        System.out.println("Configuring routes for services: User: " + URI_USER + ", Dictionary: " + URI_DICTIONARY + ", ML: " + URI_ML);

        System.out.println(ROUTES.entrySet());
        
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

    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            // Log the request details for every incoming request
            System.out.println("Incoming request: " + exchange.getRequest().getURI());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Attempt to log the redirected URI from the Location header
                System.out.println("Response status code: " + exchange.getResponse().getStatusCode());
                System.out.println("Response headers: " + exchange.getResponse().getHeaders());
                exchange.getResponse().getHeaders().getLocation();
                if (exchange.getResponse().getStatusCode() != null &&
                        exchange.getResponse().getStatusCode().is3xxRedirection() &&
                        exchange.getResponse().getHeaders().getLocation() != null) {
                    // If there's a redirection, log the Location header
                    System.out.println("Redirected URI: " + exchange.getResponse().getHeaders().getLocation());
                } else {
                    // Log that there's no redirection or the response is not yet committed
                    System.out.println("No redirection or response not yet committed for: " + exchange.getRequest().getURI());
                }
            }));
        };
    }

}
