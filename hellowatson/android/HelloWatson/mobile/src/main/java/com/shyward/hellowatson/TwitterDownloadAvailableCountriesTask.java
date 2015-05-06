package com.shyward.hellowatson;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by shyward on 5/4/15.
 */
public class TwitterDownloadAvailableCountriesTask extends AsyncTask<Void, Void, Void> {

    private TwitterCountriesObserver mObserver;
    private String TAG = TwitterDownloadAvailableCountriesTask.class.getSimpleName();


    public static interface TwitterCountriesObserver
    {
        public void onTwitterCountriesReady(int err); //0 is no error
    }

    public TwitterDownloadAvailableCountriesTask(TwitterCountriesObserver observer)
    {
        mObserver = observer;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        int err = 0;
        if(mObserver != null){
            mObserver.onTwitterCountriesReady(0);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            //builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(Defines.CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Defines.CONSUMER_SECRET);

            OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();

            builder = new ConfigurationBuilder();
            //builder.setUseSSL(true);
            builder.setApplicationOnlyAuthEnabled(true);
            builder.setOAuthConsumerKey(Defines.CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Defines.CONSUMER_SECRET);
            builder.setOAuth2TokenType(token.getTokenType());
            builder.setOAuth2AccessToken(token.getAccessToken());

            Twitter twitter = new TwitterFactory(builder.build()).getInstance();

            //QueryResult result;
            ResponseList<Location> locations;
            locations = twitter.getAvailableTrends();
            Log.i(TAG, "Showing available trends");
            for (Location location : locations) {
                //System.out.println(location.getName() + " (woeid:" + location.getWoeid() + ")");
                //Log.i("SHY WARD", "Trend name"+result.get(i));
                String countryName = location.getPlaceName();
                if(countryName.equalsIgnoreCase("Country")) {
                    //only the top 15 countries to get away the rate limit of twitter

                    boolean value = new SupportedCountries().isCountrySupported(location.getCountryName());
                    if(value) {
                        TwitterCountry tmpCountry = new TwitterCountry();
                        tmpCountry.setCountryCode(location.getCountryCode());
                        tmpCountry.setCountryName(location.getCountryName());
                        tmpCountry.setWoeid(location.getWoeid());
                        TwitterDataSingelton.getInstance().getTwitterCountries().add(tmpCountry);
                    }
                }
            }
            int length = TwitterDataSingelton.getInstance().getTwitterCountries().size();

            for (int index = 0; index < length; index++)
            {
                int woeid = TwitterDataSingelton.getInstance().getTwitterCountries().get(index).getWoeid();
                Trends trends = twitter.getPlaceTrends(woeid);
                for (int i = 0; i < trends.getTrends().length; i++) {
                    String trendString = trends.getTrends()[i].getName();
                    Log.i(TAG, trendString);
                    TwitterDataSingelton.getInstance().getTwitterCountries().get(index).getTrendingTopics().add(trendString);
                }
            }

            //now we are going to get the tweets based on each trending topic in each country
            for (int index = 0; index < length; index++)
            {
                TwitterCountry country = TwitterDataSingelton.getInstance().getTwitterCountries().get(index);
                for (String trend : country.getTrendingTopics())
                {
                    Query query = new Query(trend);
                    // YOu can set the count of maximum records here
                    query.setCount(50);
                    QueryResult result = twitter.search(query);
                    ArrayList tweets = (ArrayList) result.getTweets();
                    for (int i = 0; i < tweets.size(); i++) {
                        LocalTweet newLocalTweet = new LocalTweet();
                        newLocalTweet.setmOriginalTweet(tweets.get(i).toString());
                        newLocalTweet.setmSearchTerm(trend);
                        TwitterDataSingelton.getInstance().getTwitterCountries().get(index).getLocalTweets().add(newLocalTweet);
                    }
                }
            }
        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
