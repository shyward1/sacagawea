package com.shyward.hellowatson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableStatusCodes;

import java.util.List;

public class MainActivityWear extends Activity {

    private TextView mTextView;
    Node node; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    public final String TAG = MainActivityWear.this.getClass().getSimpleName().toString();
    public String mMessage;
    private static final int SPEECH_REQUEST_CODE = 0;
    private Button mTranslateFrenchButton;
    private Button mTranslateSpanishButton;
    private Button mQuestionButton;
    private String messageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();


        mGoogleApiClient.connect();

        setContentView(R.layout.activity_main_activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTranslateFrenchButton = (Button) stub.findViewById(R.id.translateFrenchButton);
                if(mTranslateFrenchButton != null) {
                    mTranslateFrenchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(MainActivityWear.this, "Translate French Pressed", Toast.LENGTH_SHORT).show();
                            messageType = "french:";
                            displaySpeechRecognizer();
                        }
                    });
                }
                mTranslateSpanishButton = (Button) stub.findViewById(R.id.translateSpanishButton);
                if(mTranslateSpanishButton != null) {
                    mTranslateSpanishButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(MainActivityWear.this, "Translate Spanish Pressed", Toast.LENGTH_SHORT).show();
                            messageType = "spanish:";
                            displaySpeechRecognizer();
                        }
                    });
                }
                mQuestionButton = (Button) stub.findViewById(R.id.questionButton);
                if(mQuestionButton != null) {
                    mQuestionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(MainActivityWear.this, "Question Pressed", Toast.LENGTH_SHORT).show();
                            messageType = "question:";
                            displaySpeechRecognizer();
                        }
                    });
                }


            }
        });
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            //Toast.makeText(this, spokenText, Toast.LENGTH_LONG).show();
            mMessage = messageType + spokenText;
            //mMessage.concat(spokenText);
            fireMessage();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fireMessage() {
        // Send the RPC
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                for (int i = 0; i < result.getNodes().size(); i++) {
                    Node node = result.getNodes().get(i);
                    String nName = node.getDisplayName();
                    String nId = node.getId();
                    Log.d(TAG, "Node name and ID: " + nName + " | " + nId);


                    Wearable.MessageApi.addListener(mGoogleApiClient, new MessageApi.MessageListener() {
                        @Override
                        public void onMessageReceived(MessageEvent messageEvent) {
                            Log.d(TAG, "Message received: " + messageEvent);
                        }
                    });


                    PendingResult<MessageApi.SendMessageResult> messageResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                            mMessage, null);
                    messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Status status = sendMessageResult.getStatus();
                            Log.d(TAG, "Status: " + status.toString());
                            if (status.getStatusCode() != WearableStatusCodes.SUCCESS) {
                                Toast.makeText(MainActivityWear.this, "Error - Try again", Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
                }
            }
        });
    }
}
