package com.shyward.hellowatson.api;

import android.util.Log;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

/**
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class AlphaAPI {
    private static String TAG = AlphaAPI.class.getSimpleName();
    private static AlphaAPI m_instance;
    private static String APPID = "YOUR_ID";
    private WAEngine engine;
    private AlphaAPIResultsListener m_listener;

    public static AlphaAPI getInstance() {
        Log.v(TAG, "getInstance");

        if (m_instance == null) {
            m_instance = new AlphaAPI();
        }
        return m_instance;
    }

    private AlphaAPI() {
        Log.v(TAG, "AlphaAPI Constructor");

        initializeAPI();
    }


    public void setResultsListener(AlphaAPIResultsListener listener) {
        m_listener = listener;
    }


    private void initializeAPI() {
        Log.v(TAG, "initializeAPI");
        engine = new WAEngine();

        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID(APPID);
        engine.addFormat("plaintext");
        Log.v(TAG, "finished initializing WA Engine.");
    }

    /**
     *
     * @param input text string to search for
     * @param queryResult async populated result value. interrogate this object after result listener isFinished is fired.
     * @throws AlphaAPIQueryError
     * @throws AlphaAPINoResultsException
     */
    public void createQuery(String input, AlphaAPIQueryResult queryResult) throws AlphaAPIQueryError, AlphaAPINoResultsException {
        // Create the query
        WAQuery query = engine.createQuery();

        // Set properties of the query
        query.setInput(input);

        try {
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult m_queryResult = engine.performQuery(query);
            if (queryResult == null) queryResult = new AlphaAPIQueryResult();

            if (m_queryResult.isError()) {

                String message = "Query error. Error code: " + m_queryResult.getErrorCode()
                                + " Error message: " + m_queryResult.getErrorMessage();

                Log.e(TAG, message);
                throw new AlphaAPIQueryError(message);

            } else if (!m_queryResult.isSuccess()) {
                System.out.println("Query was not understood; no results available.");
                Log.w(TAG, "Query was not understood; no results available.");

                throw new AlphaAPINoResultsException("Query was not understood; no results available.");
            } else {
                // Got a result
                System.out.println("Successful query. Pods follow:\n");
                Log.v(TAG, "Successful query. Pods follow:");
                queryResult.setWaQueryResult(m_queryResult);

                for (WAPod pod : m_queryResult.getPods()) {
                    if (!pod.isError()) {



                        Log.v(TAG, "title: " + pod.getTitle());
                        if ("Result".equals(pod.getTitle())) {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {

                                        Log.v(TAG, "text: " + ((WAPlainText) element).getText());
                                        queryResult.setResult(((WAPlainText) element).getText());

                                        if (m_listener != null) {
                                            m_listener.onFinish();
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        Log.v(TAG, "text: " + ((WAPlainText) element).getText());

                                    }
                                }
                            }
                        }
                    }
                }
                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
            }
        } catch (WAException e) {
            e.printStackTrace();
        }
    }



}


