package app.linguistai.gateway.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super("Please log in", HttpStatus.UNAUTHORIZED);
    }
}
