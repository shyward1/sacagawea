package com.shyward.hellowatson;

import java.util.ArrayList;

/**
 * Created by shyward on 5/4/15.
 */
public class TwitterCountry {

    private String countryName;
    private String CountryCode;
    private int woeid;
    private ArrayList<String> trendingTopics = new ArrayList<String>();
    private ArrayList<LocalTweet> localTweets = new ArrayList<LocalTweet>();


    public ArrayList<LocalTweet> getLocalTweets() {
        return localTweets;
    }

    public void setLocalTweets(ArrayList<LocalTweet> localTweets) {
        this.localTweets = localTweets;
    }


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

    public ArrayList<String> getTrendingTopics() {
        return trendingTopics;
    }

    public void setTrendingTopics(ArrayList<String> trendingTopics) {
        this.trendingTopics = trendingTopics;
    }

}
