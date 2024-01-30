package app.linguistai.gateway.exception;

import org.springframework.http.HttpStatus;

public class JWTException extends CustomException {

    public JWTException() {
        super("Validation Error!", HttpStatus.UNAUTHORIZED);
    }

    public JWTException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public JWTException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }

    public JWTException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
    }
}
