package com.shyward.hellowatson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;

public class WatsonWearService extends WearableListenerService {

    private String TAG = WatsonWearService.class.getSimpleName().toString();
    private GoogleApiClient mGoogleApiClient;

    private static boolean mAuthenticated= false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        //  Needed for communication between watch and device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        tellWatchConnectedState("connected");
                        //  "onConnected: null" is normal.
                        //  There's nothing in our bundle.
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();


        mGoogleApiClient.connect();

    }


    @Override public void onDestroy()
    {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }


    /**
     * Here, the device actually receives the message that the phone sent, as a path.
     * We simply check that path's last segment and act accordingly.
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {


        Log.v(TAG, "msg rcvd");
        Log.v(TAG, messageEvent.getPath());
        String messageType = messageEvent.getPath();
        if(messageType.contains("spanish:"))
        {
            String question = messageType.replace("spanish:","");
            //Toast.makeText(WatsonWearService.this, "Firing Async Task spanish translate", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Firing Async Task spanish Translate");
            WatsonTranslate2 tmpTask = new WatsonTranslate2(getApplicationContext());
            tmpTask.setTextToTranslate(question);
            tmpTask.setDesitedLanguage("spanish");
            tmpTask.execute();
        }
        if(messageType.contains("french:"))
        {
            String question = messageType.replace("french:","");
            ///Toast.makeText(WatsonWearService.this, "Firing Async Task french translate", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Firing Async Task french Translate");
            WatsonTranslate2 tmpTask = new WatsonTranslate2(getApplicationContext());
            tmpTask.setTextToTranslate(question);
            tmpTask.setDesitedLanguage("french");
            tmpTask.execute();
        }
        else if (messageType.contains("question:"))
        {
            String question = messageType.replace("question:","");
            ///Toast.makeText(WatsonWearService.this, "Firing Async Task question", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Firing Async Task question:" +question);
            QuestionAysncTask tmpTask = new QuestionAysncTask(getApplicationContext());
            tmpTask.setQueryString(question);
            tmpTask.execute();

        }

    }


    private void tellWatchConnectedState(final String state){


        new AsyncTask<Void, Void, List<Node>>(){


            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }


            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.v(TAG, "telling " + node.getId() + " i am " + state);


                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            "/listener/watson/" + state,
                            null
                    );


                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v(TAG, "Phone: " + sendMessageResult.getStatus().getStatusMessage());
                        }
                    });
                }
            }
        }.execute();


    }

    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }
}
