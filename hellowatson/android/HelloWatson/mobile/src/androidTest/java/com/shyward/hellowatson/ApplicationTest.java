package com.shyward.hellowatson;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.shyward.hellowatson.api.AlphaAPI;
import com.shyward.hellowatson.api.AlphaAPINoResultsException;
import com.shyward.hellowatson.api.AlphaAPIQueryError;
import com.shyward.hellowatson.api.AlphaAPIQueryResult;
import com.shyward.hellowatson.api.AlphaAPIResultsListener;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> implements AlphaAPIResultsListener {
    public static final String TAG = ApplicationTest.class.getSimpleName();

    public AlphaAPIQueryResult queryResult;

    public ApplicationTest() {
        super(Application.class);
    }



    public void testAlphaAPI() {
        AlphaAPI alphaAPI = AlphaAPI.getInstance();
        queryResult = new AlphaAPIQueryResult();
        alphaAPI.setResultsListener(this);

        try {
            alphaAPI.createQuery("When was the brooklyn bridge built", queryResult);


            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        catch (AlphaAPINoResultsException noresults) {
            noresults.printStackTrace();
        }
        catch (AlphaAPIQueryError queryError) {
            queryError.printStackTrace();
        }



        assertTrue(true);
    }


    @Override
    public void onFinish() {
        Log.v(TAG, "onFinish called");

        if (queryResult != null) {
            Log.v(TAG, "Answer is: "+ queryResult.getResult());
        }
        else {
            Log.w(TAG, "query result is null");
        }
    }
}