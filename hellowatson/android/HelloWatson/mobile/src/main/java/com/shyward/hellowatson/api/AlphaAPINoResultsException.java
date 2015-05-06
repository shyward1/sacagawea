package com.shyward.hellowatson.api;

/**
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class AlphaAPINoResultsException extends Exception {
    public AlphaAPINoResultsException(String message) {
        super(message);
    }

    public AlphaAPINoResultsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
