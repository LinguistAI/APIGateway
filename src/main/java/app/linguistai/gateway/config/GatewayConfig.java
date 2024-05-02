package app.linguistai.gateway.config;

import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Autowired
    JWTFilter filter;
    
    @Value("${spring.base.prefix}")
    private String BASE_PREFIX;

    @Value("${spring.uri.user}")
    private String URI_USER;
    
    @Value("${spring.uri.dictionary}")
    private String URI_DICTIONARY;

    @Value("${spring.uri.ml}")
    private String URI_ML;

    @Value("${spring.uri.aws}")
    private String URI_AWS;

    @Value("${spring.uri.aws.stage}")
    private String URI_AWS_STAGE;

    public static final String DICTINOARY_SERVICE_ID = "dictionary-service";
    public static final String USER_SERVICE_ID = "user-service";
    public static final String ML_ID = "ml-service";
    public static final String AWS_ID = "aws-services";

    public static final HashMap<String, String> ROUTES = new HashMap<>();

    @PostConstruct
    public void init() {
        ROUTES.put(AWS_ID, BASE_PREFIX + "/aws/**");
        ROUTES.put(DICTINOARY_SERVICE_ID, BASE_PREFIX + "/dictionary/**");
        ROUTES.put(ML_ID, BASE_PREFIX + "/ml/**");
        ROUTES.put(USER_SERVICE_ID, BASE_PREFIX + "/**");

        System.out.println("Routes initialized: " + ROUTES);
    }


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        System.out.println("Configuring routes for services: User: " + URI_USER + ", Dictionary: " + URI_DICTIONARY + ", ML: " + URI_ML);
        System.out.println("Configuring routes for AWS: URI: " + URI_AWS + ", STAGE: " + URI_AWS_STAGE);

        System.out.println(ROUTES.entrySet());
        
        return builder.routes()
                .route(AWS_ID, r -> r.path(ROUTES.get(AWS_ID))
                        .filters(f -> f.rewritePath("/api/v1/aws/(?<segment>.*)", "/testing/${segment}")
                        .filter(filter))
                        .uri(URI_AWS + URI_AWS_STAGE))
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
                exchange.getResponse().getHeaders().getLocation();
                if (exchange.getResponse().getStatusCode() != null &&
                        exchange.getResponse().getStatusCode().is3xxRedirection() &&
                        exchange.getResponse().getHeaders().getLocation() != null) {
                    // If there's a redirection, log the Location header
                    System.out.println("Redirected URI: " + exchange.getResponse().getHeaders().getLocation());
                }
                // Construct the redirected URI without specifying the port
                // String redirectedURI = exchange.getResponse().getHeaders().getLocation().toString();
                // if (redirectedURI.contains(":443")) {
                //     redirectedURI = redirectedURI.replace(":443", "");
                // }
                // System.out.println("Redirected to: " + redirectedURI);
                
                String redirectedURI = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR).toString();
                System.out.println("Redirected to: " + redirectedURI);
            }));
        };
    }

}
