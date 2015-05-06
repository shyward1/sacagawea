package com.shyward.hellowatson;

import android.os.AsyncTask;
import android.util.Log;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class AlchemyAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = AlchemyAsyncTask.class.getSimpleName();
    private static final String BASE_URL = "https://twitter.com/";

    private AlchemyAPI api = null;
    private Hashtable<String, ArrayList> data;

    public AlchemyAsyncTask() {
        try {
            api = AlchemyAPI.GetInstanceFromString(Defines.IBM_ALCHEMY_API_KEY);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Error loading AlchemyAPI.  Check that you have a valid AlchemyAPI key set in the AlchemyAPI_Key variable.", ex);
        }

        // holds all of the sentiment data
        // Country, List of TwitterTrendingTopic
        data = new Hashtable<String, ArrayList>();
    }

    public Hashtable<String, ArrayList>getSentimentData() {
        return this.data;
    }

    /**
     * Loops through all countries, and all trending topics
     * creates a TwitterTrendingTopic with the topic string and sentiment score
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        String uri = null;
        Document doc = null;
        String formattedTrend;

        TwitterDataSingelton twitterData = TwitterDataSingelton.getInstance();
        //Log.v(TAG, "number of countries is " + twitterData.getTwitterCountries().size());

        for (TwitterCountry country : twitterData.getTwitterCountries()) {
            //TwitterCountry country = twitterData.getTwitterCountries().get(1);
            //Log.v(TAG, "number of trends in "+ country.getCountryName() +" is "+ country.getTrendingTopics().size());

            // instantiate a new ArrayList for this country
            ArrayList<TwitterTrendingTopic> trendingTopicList = new ArrayList<TwitterTrendingTopic>();

            for (String trend : country.getTrendingTopics()) {
                if (trend == null) {
                    continue;
                }
                // construct a url for this trending topic
                if (trend.startsWith("#")) {
                    formattedTrend = trend.substring(1);
                    uri = "hashtag/" + formattedTrend + "?f=realtime&src=tren";
                } else {
                    // replace spaces
                    formattedTrend = trend.replaceAll(" ", "%20");
                    uri = "search?q=%22" + formattedTrend + "%22&f=realtime&src=tren";
                }
                Log.v(TAG, "trend url: " + BASE_URL + uri);

                try {
                    TwitterTrendingTopic topic = getSentimentForUrl(BASE_URL + uri, trend);

                    // add to the ArrayList
                    trendingTopicList.add(topic);

                } catch (Exception ignore) {
                    Log.e(TAG, "Caught exception calling Alchemy Sentiment API.", ignore);
                }
            }   // trending topics list

            // add this country's trending topics to the hashtable
            data.put(country.getCountryName(), trendingTopicList);
        }     // for country

        // when all finished save the data in TwitterDataSingleton
        TwitterDataSingelton.getInstance().setSentimentData(data);

        // notify the listener
        TwitterDataSingelton.getInstance().onSentimentDataReady(0);

        return null;
    }

    private TwitterTrendingTopic getSentimentForUrl(String url, String topicString) throws Exception {
        TwitterTrendingTopic topic = null;
        Document doc;
        AlchemyAPI_NamedEntityParams nep = new AlchemyAPI_NamedEntityParams();
        nep.setSentiment(true);
        //doc = api.URLGetTextSentiment(url, nep);
        doc = api.URLGetTextSentiment(url);
        //doc = api.URLGetRankedNamedEntities("https://twitter.com/hashtag/MetGala?src=tren&event_id=TREND_CONTEXT%3A595412374653394944", nep);
        //doc = api.TextGetTextSentiment("https://twitter.com/hashtag/MetGala?src=tren&event_id=TREND_CONTEXT%3A595412374653394944", nep);
        //Log.v(TAG, "Sentiment API Result: "+ doc);

        topic = parseXmlResult(doc, topicString);
        return topic;
    }


    public TwitterTrendingTopic parseXmlResult(final Document doc, final String topicString) {
        TwitterTrendingTopic topic = new TwitterTrendingTopic();
        topic.setTopic(topicString);

        if (doc == null) {
            return null;
        }

        Element root = doc.getDocumentElement();
        NodeList sentiments = root.getElementsByTagName("docSentiment");
        //Log.v(TAG, "there are "+ items.getLength() + " docSentiment items");
        NodeList items = sentiments.item(0).getChildNodes();

        //NodeList sentiments = root.getElementsByTagName("docSentiment");
        for (int i = 0; i < items.getLength(); i++) {
            Node anode = items.item(i);
            String aname = anode.getNodeName();
            if ("score".equals(aname)) {
                try {
                    float score = Float.parseFloat(anode.getChildNodes().item(0).getNodeValue());
                    Log.v(TAG, "score is " + score);
                    topic.setScore(score);
                } catch (NumberFormatException ignore) {
                    ignore.printStackTrace();
                }
            } else if ("type".equals(aname)) {
                String type = anode.getChildNodes().item(0).getNodeValue();
                Log.v(TAG, "type is " + type);
                if ("positive".equals(type)) {
                    topic.setSentimentType(TwitterTrendingTopic.Sentiment.Positive);
                } else if ("neutral".equals(type)) {
                    topic.setSentimentType(TwitterTrendingTopic.Sentiment.Neutral);
                } else if ("negative".equals(type)) {
                    topic.setSentimentType(TwitterTrendingTopic.Sentiment.Negative);
                }
            }
        }

        return topic;
    }
}
