package com.shyward.hellowatson;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.shyward.hellowatson.api.AlphaAPI;
import com.shyward.hellowatson.api.AlphaAPINoResultsException;
import com.shyward.hellowatson.api.AlphaAPIQueryError;
import com.shyward.hellowatson.api.AlphaAPIQueryResult;
import com.shyward.hellowatson.api.AlphaAPIResultsListener;

/**
 * Created by shyward on 5/4/15.
 */
public class QuestionAysncTask extends AsyncTask<Void, Void, Void> implements AlphaAPIResultsListener, StringToSpeech.SpeechEngineReadyObserver {

    public static final String TAG = QuestionAysncTask.class.getSimpleName();

    public AlphaAPIQueryResult queryResult;
    private String queryString;
    private StringToSpeech mStringToSpeech;
    private Context mContext;


    public  QuestionAysncTask(Context context)
    {
        mContext = context;
    }
    @Override
    protected Void doInBackground(Void... params) {

        AlphaAPI alphaAPI = AlphaAPI.getInstance();
        queryResult = new AlphaAPIQueryResult();
        alphaAPI.setResultsListener(this);

        try {
            alphaAPI.createQuery(queryString, queryResult);


        } catch (AlphaAPINoResultsException e) {
            e.printStackTrace();
        } catch (AlphaAPIQueryError alphaAPIQueryError) {
            alphaAPIQueryError.printStackTrace();
        }

        return null;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }


    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        int err = 0;
    }

    @Override
    public void onFinish() {

        //lets speak that answer
        Log.v(TAG, "onFinish called");

        if (queryResult != null) {
            Log.v(TAG, "Answer is: "+ queryResult.getResult());
            mStringToSpeech = new StringToSpeech(queryResult.getResult(), mContext, this);
            mStringToSpeech.setTargetLanguage("english");
        }
        else {
            Log.w(TAG, "query result is null");
        }

    }

    @Override
    public void onSpeechEngineReady() {
        //the speech engine is ready to speak
        //make it so
        mStringToSpeech.speakText();
    }
}
