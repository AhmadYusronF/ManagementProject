package com.menejementpj.test;

public class Debug {
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    private static void log(String level, String color, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace.length > 3 ? stackTrace[3] : stackTrace[2];

        String fileName = caller.getFileName();
        int lineNumber = caller.getLineNumber();
        String methodName = caller.getMethodName();

        String logMessage = String.format(
                "%s[%s] %s:%d (%s)%s ➜ %s",
                color, level, fileName, lineNumber, methodName, RESET, message);

        System.out.println(logMessage);
    }

    public static void info(String message) {
        log("INFO", CYAN, message);
    }

    public static void warn(String message) {
        log("WARN", YELLOW, message);
    }

    public static void error(String message) {
        log("ERROR", RED, message);
    }

    public static void success(String message) {
        log("SUCCESS", GREEN, message);
    }

    public static void log(String message) {
        info(message);
    }
}
