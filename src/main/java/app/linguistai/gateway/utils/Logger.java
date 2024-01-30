package app.linguistai.gateway.utils;

import java.text.SimpleDateFormat;

import app.linguistai.gateway.consts.LogType;

public class Logger {
    private static final String DATE_FORMAT = "yyyy.MM.dd HH.mm.ss";

    private static String createLog(String msg, String info) {        
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new java.util.Date());

        return String.format("%s %s: %s", timestamp, info, msg);
    }

    public static void log(String msg) {
        System.out.println(createLog(msg, LogType.INFO));
    }

    public static void error(String msg) {
        System.out.println(createLog(msg, LogType.ERROR));
    }
}
