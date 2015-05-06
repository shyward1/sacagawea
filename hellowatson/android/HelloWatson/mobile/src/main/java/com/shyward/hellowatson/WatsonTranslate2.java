package com.shyward.hellowatson;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.machine_translation.v1.MachineTranslation;
import com.ibm.watson.developer_cloud.machine_translation.v1.model.Language;
/**
 * Created by shyward on 5/4/15.
 */
public class WatsonTranslate2 extends AsyncTask<Void, Void, Void> implements StringToSpeech.SpeechEngineReadyObserver{

    private String TAG = WatsonTranslate2.class.getSimpleName();
    private Context mContext;
    private String mTextTranslate;
    private String mDesiredLang;
    private Language mIBMLang;
    private StringToSpeech mStringToSpeech;


    public WatsonTranslate2(Context context) {
        mContext = context;
    }

    public void setTextToTranslate(String text)
    {
        mTextTranslate = text;
    }

    public void setDesitedLanguage(String language)
    {
        mDesiredLang = language;
        if(mDesiredLang.equalsIgnoreCase("spanish"))
        {
            mIBMLang = Language.SPANISH;
        }
        else if(mDesiredLang.equalsIgnoreCase("french"))
        {
            mIBMLang = Language.FRENCH;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            MachineTranslation service = new MachineTranslation();
            service.setUsernameAndPassword(Defines.IBM_BLUEMIX_USERNAME, Defines.IBM_BLUEMIX_PASSWORD);
            String response = service.translate(mTextTranslate, Language.ENGLISH, mIBMLang);
            System.out.println(response);
            //here we feed to speech task to talk
            mStringToSpeech = new StringToSpeech(response, mContext, this);
            mStringToSpeech.setTargetLanguage(mDesiredLang);
        } catch (Exception w) {
            String mess = w.getMessage();
            Throwable some = w.getCause();
            Log.i(TAG, mess);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        int err = 0;
        //need to speak the results
    }

    @Override
    public void onSpeechEngineReady() {
        //okay say it
        //i'm very sleepy
        mStringToSpeech.speakText();
    }
}
