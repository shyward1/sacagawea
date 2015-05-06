package com.shyward.hellowatson;

/**
 * Created by Mark Reimer on 5/5/15.
 * Copyright © ADT LLC, 2015
 */
public class TwitterTrendingTopic {
    enum Sentiment {
        Positive, Negative, Neutral;
    }

    private String topic;
    private Sentiment sentimentType;
    private float score;

    public TwitterTrendingTopic() {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Sentiment getSentimentType() {
        return sentimentType;
    }

    public void setSentimentType(Sentiment sentimentType) {
        this.sentimentType = sentimentType;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
