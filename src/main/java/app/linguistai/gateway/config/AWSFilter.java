package app.linguistai.gateway.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import app.linguistai.gateway.exception.JWTException;
import reactor.core.publisher.Mono;

@Component
public class AWSFilter implements GatewayFilter {
    @Autowired
    private RouterValidator routerValidator;
   
	@Value("${spring.aws.api.key}")
	private String AWS_KEY;

    private String AWS_KEY_HEADER = "x-api-key";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // if request endpoint is not AWS filter, do not apply filter
        if (!routerValidator.isAWS.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        try {
            List<String> awsHeader = exchange.getRequest().getHeaders().get(AWS_KEY_HEADER);
            if (awsHeader != null && !awsHeader.isEmpty()) {
                // remove token from the header
                exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(AWS_KEY_HEADER));
            }      

            // add aws api key to the header
            exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(AWS_KEY_HEADER, AWS_KEY));
            System.out.println(exchange.getRequest().getHeaders());

            return chain.filter(exchange);
        } catch (Exception e) {
            return Mono.error(new JWTException("Something went wrong!"));
        }
    }
}
