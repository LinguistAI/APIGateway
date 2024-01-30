package app.linguistai.gateway.exception;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.springframework.http.HttpStatus;

public class Response {

    private static final String DATE_FORMAT = "yyyy.MM.dd HH.mm.ss";

    public static HashMap<String, Object> create(String message, HttpStatus status, Object data) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status.value());
        response.put("data", data);

        return response;
    }

    public static HashMap<String, Object> create(String message, HttpStatus status) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status.value());

        return response;
    }

    public static HashMap<String, Object> create(String message, int status) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

        response.put("timestamp", timestamp);
        response.put("msg", message);
        response.put("status", status);

        return response;
    }
}