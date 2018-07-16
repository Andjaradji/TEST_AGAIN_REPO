package com.vexanium.vexgift.http;

/**
 * Created by mac on 11/16/17.
 */

public class ApiException extends Exception {
    public static final String API_INVALID_SESSION = "Invalid session";

    public ApiException(String msg)
    {
        super(msg);
    }
}
