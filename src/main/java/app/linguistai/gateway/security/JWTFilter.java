package app.linguistai.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import app.linguistai.consts.Header;
import reactor.core.publisher.Mono;

@Component
public class JWTFilter implements GatewayFilter {

    private final static String TOKEN_BEARER_PREFIX = "Bearer";

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // if request endpoint is included in the whitelist, do not apply filter
        System.out.println("current request:" + exchange.getRequest().getPath().value());
        if (!routerValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // TODO fix this code
        String tokenHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String username = "";
        String token = "";
        String currentEndpoint = exchange.getRequest().getPath().value();

        if (tokenHeader == null || !tokenHeader.startsWith(TOKEN_BEARER_PREFIX)) {
            return Mono.error(new Exception("Token is not found"));
        }

        try {
            token = getTokenWithoutBearer(tokenHeader);
        } catch (Exception exc) {
            // Handle exception
            return Mono.error(exc);
        }

        boolean isCurrentRefresh = currentEndpoint.equals("/api/v1/auth/refresh");

        try {
            // if token in the header is refresh token and it is expired, send error message
            if (isCurrentRefresh && jwtUtils.isRefreshTokenExpired(token)) {
                return Mono.error(new Exception("Refresh token is invalid"));
            }

            // if token in the header is access token and it is expired, send error message
            if (!isCurrentRefresh && jwtUtils.isAccessTokenExpired(token)) {
                return Mono.error(new Exception("Access token is invalid"));
            }

            // extract the token based on its type
            if (isCurrentRefresh) {
                username = jwtUtils.extractRefreshUsername(token);
            } else {
                username = jwtUtils.extractAccessUsername(token);
            }

            final String USERNAME = username;

            // if username cannotbe extracted from the token, send error message
            if (USERNAME == null) {
                return Mono.error(new Exception("Username is not valid"));
            }

            if (!isCurrentRefresh) {
                // remove token from the header, so that microservices do not need to authorize users
                exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION));
            }            

            // add username and role to the headers, so that microservices can use it
            exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(Header.USER_EMAIL, USERNAME));

            return chain.filter(exchange);
        } catch (Exception e) {
            // Handle exceptions
            return Mono.error(e);
        }
    }

    private String getTokenWithoutBearer(String token) throws Exception {
        if (token == null) {
            throw new Exception("Token is not found!");
        }

        return token.substring(TOKEN_BEARER_PREFIX.length());
    }
}
