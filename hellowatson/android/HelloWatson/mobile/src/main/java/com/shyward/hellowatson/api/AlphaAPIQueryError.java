package com.shyward.hellowatson.api;

/**
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class AlphaAPIQueryError extends Exception {

    public AlphaAPIQueryError(String message) {
        super(message);
    }

    public AlphaAPIQueryError(String message, Throwable throwable) {
        super(message, throwable);
    }
}
