package com.shyward.hellowatson;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by shyward on 5/4/15.
 */
public class StringToSpeech {

    private TextToSpeech mTts;
    private String mStringToSpeak;
    private Context mContext;
    private Activity mParentReference;
    private SpeechEngineReadyObserver mObserver;
    private Locale mLocale;

    public static interface SpeechEngineReadyObserver
    {
        public void onSpeechEngineReady();
    }

    public void setTargetLanguage(String targetLanguage)
    {
        if(targetLanguage.equalsIgnoreCase("french"))
        {
            mLocale = Locale.FRENCH;
            //mLocale = new Locale("fr_FR");
            //mLocale = new Locale ("spa", "ESP");
        }
        else if (targetLanguage.equalsIgnoreCase("spanish"))
        {
            mLocale = new Locale("es_ES");
            mLocale = new Locale ("spa", "ESP");

        }
        if(targetLanguage.equalsIgnoreCase("english"))
        {
            mLocale = Locale.US;
            //mLocale = new Locale("fr_FR");
            //mLocale = new Locale ("spa", "ESP");
        }
    }
    public StringToSpeech(String stringToSpeak, Context context, SpeechEngineReadyObserver observer) {
        mStringToSpeak = stringToSpeak;
        mContext = context;
        mObserver = observer;

        mTts = new TextToSpeech(mContext,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            mTts.setLanguage(mLocale);
                            mObserver.onSpeechEngineReady();
                        }
                    }
                });


    }
    public void stopSpeaking(){
        if(mTts !=null){
            mTts.stop();
            mTts.shutdown();
        }
    }
    public void speakText(){

        //Toast.makeText(mContext, mStringToSpeak,
        //Toast.LENGTH_SHORT).show();
        mTts.speak(mStringToSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }
}
