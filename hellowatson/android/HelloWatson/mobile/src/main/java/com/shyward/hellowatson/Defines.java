package com.shyward.hellowatson;

/**
 * Created by shyward on 5/4/15.
 */
public class Defines {

    public final static String TWITTER_URL_TRENDS_AVAILABLE_COUNTRIES = "https://api.twitter.com/1.1/trends/available.json";
    final static String CONSUMER_KEY = "YOUR_KEY";
    final static String CONSUMER_SECRET = "YOUR_SECRET";
    public  static String ACCESS_TOKEN = "YOUR_TOKEN";
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

    final static public long fiveHourWindowForDataRefresh = 60*60*5; //sec*min*hour

    public final static boolean DEBUG = true;

    public static final String IBM_ALCHEMY_API_KEY = "YOUR_KEY";

    public static final String IBM_BLUEMIX_USERNAME = "YOUR_USERNAME";
    public static final String IBM_BLUEMIX_PASSWORD = "YOUR_PASSWORD";

    public static final String IBM_LANGUAGE_IDENTIFICATION_BLUEMIX_USER = "YOUR_USER";
    public static final String IBM_LANGUAGE_IDENTIFICATION_BLUEMIX_PASS = "YOUR_PASS";

    public static final String UBER_CLIENT_KEY = "UBER_KEY";
    public static final String UBER_CLIENT_TOKEN = "UBER_TOKEN";

}
