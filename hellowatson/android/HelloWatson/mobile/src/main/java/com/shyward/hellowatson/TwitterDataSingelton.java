package com.shyward.hellowatson;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by shyward on 5/4/15.
 */
public class TwitterDataSingelton implements TwitterDownloadAvailableCountriesTask.TwitterCountriesObserver {

    private static final String TAG = TwitterDataSingelton.class.getSimpleName();
    private Activity mActivity;
    private static TwitterDataSingelton mInstance = null;
    private TwitterDownloadAvailableCountriesTask mDownloadCountryTask;
    private long mTimestampDataWasRetrieved = 0;
    private boolean mIsDataStale = true;
    private Context mContext;
    // sentiment data for twitter, by country, by trending topic
    private Hashtable<String, ArrayList> data;

    public Hashtable<String, ArrayList> getSentimentData() {
        return data;
    }

    public void setSentimentData(Hashtable<String, ArrayList> data) {
        this.data = data;
    }

    private ArrayList<TwitterCountry> mTwitterCountries = new ArrayList<TwitterCountry>();

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public boolean isIsDataStale() {
        return mIsDataStale;
    }

    public void setIsDataStale(boolean mIsDataStale) {
        this.mIsDataStale = mIsDataStale;
    }


    public static TwitterDataSingelton getInstance() {
        if (mInstance == null) {
            mInstance = new TwitterDataSingelton();
        }
        return mInstance;
    }

    protected TwitterDataSingelton() {
        // Exists only to defeat instantiation.
    }

    public ArrayList<TwitterCountry> getTwitterCountries() {
        return mTwitterCountries;
    }

    public void setTwitterCountries(ArrayList<TwitterCountry> mTwitterCountries) {
        this.mTwitterCountries = mTwitterCountries;
    }

    public void downloadTrendingCountries()
    {
            ArrayList<TwitterCountry> countries = readPrefsData();
            if (countries == null) {
                checkTimestamp();
                if (isIsDataStale()) {
                    mDownloadCountryTask = new TwitterDownloadAvailableCountriesTask(this);
                    mDownloadCountryTask.execute();
                } else {
                    setTwitterCountries(countries);
                }
                logCountryData();
                //Toast.makeText(mContext, "Pulled new twitter data", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(mContext, "Using old twitter data", Toast.LENGTH_SHORT).show();
                setTwitterCountries(countries);
                logCountryData();
            }
    }

    public void downloadSentiment() {
        Log.v(TAG, "Download Sentiment");

        Hashtable<String, ArrayList> sentimentData = readSentimentData();
        if (sentimentData == null) {
            checkTimestamp();
            if (isIsDataStale()) {
                // kick off background process to call Alchemy Sentiment API
                new AlchemyAsyncTask().execute();
            } else {
                setSentimentData(sentimentData);
            }
            //Toast.makeText(mContext, "Pulled new sentiment  data", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(mContext, "Using cached sentiment data", Toast.LENGTH_SHORT).show();
            setSentimentData(sentimentData);
        }
    }

    public void checkTimestamp()
    {
        long currenttimestamp = System.currentTimeMillis() / 1000;
        if(mTimestampDataWasRetrieved+Defines.fiveHourWindowForDataRefresh > currenttimestamp)
        {
            //five hours since last update
            setIsDataStale(true);
        }
    }

    public void onSentimentDataReady(int err) {
        writeSentimentDataToPrefs();
    }

    @Override
    public void onTwitterCountriesReady(int err) {
        //we have downloaded and parsed the data from twitter
        setIsDataStale(false);
        mTimestampDataWasRetrieved = System.currentTimeMillis() / 1000;
        writeDatatoPrefs();
        if(Defines.DEBUG)
        {
            logCountryData();
        }
    }

    public void writeSentimentDataToPrefs() {
        Log.v(TAG, "writing Sentiment Data to Prefs.");
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String jsonTwitterSentiments = gson.toJson(this.data);

        prefsEditor.putString("TwitterSentimentData", jsonTwitterSentiments);
        prefsEditor.commit();
    }

    public Hashtable<String, ArrayList> readSentimentData()
    {
        Log.v(TAG, "Reading Sentiment data from Prefs.");
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("TwitterSentimentData", "");
        Type type = new TypeToken<Hashtable<String, ArrayList<TwitterTrendingTopic>>>(){}.getType();
        Hashtable<String, ArrayList> sentimentData = gson.fromJson(json, type);
        return sentimentData;
    }

    public void writeDatatoPrefs()
    {
        //lets store this data in shared prefs

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String jsonCountries = gson.toJson(getTwitterCountries());
        Log.d("TAG","jsonTwitterCountries = " + jsonCountries);
        prefsEditor.putString("MyTwitterData", jsonCountries);
        prefsEditor.commit();
    }

    public ArrayList<TwitterCountry> readPrefsData()
    {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyTwitterData", "");
        Type type = new TypeToken<ArrayList<TwitterCountry>>(){}.getType();
        ArrayList<TwitterCountry> countryList = gson.fromJson(json, type);
        return countryList;
    }

    public void logCountryData()
    {
        for (TwitterCountry country : getTwitterCountries())
        {
            //String concatTrends = "Trends: ";
            StringBuilder value = new StringBuilder();
            value.append("Trends: ");
            for (String trend : country.getTrendingTopics())
            {
                value.append(", "+trend);
            }
            Log.i(TAG, "Country Name: " + country.getCountryName() + " " + value);
        }

    }

    public TwitterTrendingTopic.Sentiment getSentimentForCountry(String countryName) {
        TwitterTrendingTopic.Sentiment sentiment = TwitterTrendingTopic.Sentiment.Neutral;

        ArrayList<TwitterTrendingTopic> sentimentList = data.get(countryName);
        if (sentimentList != null) {
            int score = 0;

            for (Iterator<TwitterTrendingTopic> it = sentimentList.iterator(); it.hasNext(); ) {
                TwitterTrendingTopic.Sentiment s = it.next().getSentimentType();
                if (s == TwitterTrendingTopic.Sentiment.Negative) {
                    --score;
                }
                else if (s == TwitterTrendingTopic.Sentiment.Positive) {
                    ++score;
                }
            }
            if (score > 0) sentiment = TwitterTrendingTopic.Sentiment.Positive;
            else if (score < 0) sentiment = TwitterTrendingTopic.Sentiment.Negative;
            else sentiment = TwitterTrendingTopic.Sentiment.Neutral;
        }

        return sentiment;
    }

}
