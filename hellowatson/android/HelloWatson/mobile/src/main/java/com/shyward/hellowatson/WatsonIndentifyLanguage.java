package com.shyward.hellowatson;

import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.language_identification.v1.LanguageIdentification;
import com.ibm.watson.developer_cloud.language_identification.v1.model.IdentifiedLanguage;

/**
 * Created by shyward on 5/4/15.
 */
public class WatsonIndentifyLanguage extends AsyncTask<Void, Void, Void> {

    private String TAG = WatsonIndentifyLanguage.class.getSimpleName();
    private String mLanguageDetected;

    public WatsonIndentifyLanguage()
    {

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {


            LanguageIdentification service = new LanguageIdentification();
            service.setUsernameAndPassword(Defines.IBM_LANGUAGE_IDENTIFICATION_BLUEMIX_USER, Defines.IBM_LANGUAGE_IDENTIFICATION_BLUEMIX_PASS);
            service.setApiKey("YOUR_KEY");
            IdentifiedLanguage lang = service.identify("The language identification service takes text input and identifies the language used.");
            Log.i(TAG, lang.toString());
            setLanguageDetected(lang.toString());
        }
        catch (Exception w)
        {
            String mess = w.getMessage();
            Throwable some = w.getCause();
            Log.i(TAG, mess);
        }
        return null;
    }

    public String getLanguageDetected() {
        return mLanguageDetected;
    }

    public void setLanguageDetected(String mLanguageDetected) {
        this.mLanguageDetected = mLanguageDetected;
    }
}
