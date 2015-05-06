package com.shyward.hellowatson;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MainActivityMobile extends FragmentActivity implements StringToSpeech.SpeechEngineReadyObserver,
        EarthFragment.OnEarthFragmentInteractionListener, PlacesFragment.OnPlacesFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private StringToSpeech mStringToSpeech;
    private String mTestString = "Je voudrais commander 2 bières . Merci";
    private String mTestSpansihString = "Me gustaría pedir 2 cervezas. gracias";
    private FragmentManager mFragmentManager = null;
    public final String GLOBE_FRAGMENT_TAG = "Globe_Fragment_Tag";
    public final String PLACE_FRAGMENT_TAG = "Place_Fragment_Tag";
    private static int REQUEST_PLACE_PICKER = 101;
    public static TextView mHelloWorld;
    public static Context mContext;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public GoogleMap mGoogleMap;
    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
    private CardView mUberCard;
    private CardView mPlacesCard;
    private ImageView mFaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_mobile);
        mContext = getApplicationContext();

        buildGoogleApiClient();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

        mFaceView = (ImageView) findViewById(R.id.face_based_on_sentiment);

        mUberCard = (CardView) findViewById(R.id.card_view_uber);
        if(mUberCard != null)
        {
            Drawable tmpDrawable = getResources().getDrawable(R.drawable.uberbanner_smaller);
            mUberCard.setBackground(tmpDrawable);

            mUberCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mLastLocation != null) {
                        PackageManager pm = mContext.getPackageManager();
                        try
                        {
                            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                            // Do something awesome - the app is installed! Launch App.
                            //uber://?action=setPickup&pickup=my_location
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("uber://?action=setPickup&pickup=my_location")));
                        }
                        catch (PackageManager.NameNotFoundException e)
                        {
                            // No Uber app! Open Mobile Website.
                        }
                    }
                }
            });
        }
        mPlacesCard = (CardView) findViewById(R.id.card_view_places);
        if(mPlacesCard != null)
        {
            Drawable tmpDrawable = getResources().getDrawable(R.drawable.places_banner);
            mPlacesCard.setBackground(tmpDrawable);

            mPlacesCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPickButtonClick();
                }
            });
        }

    /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    */
        //mStringToSpeech = new StringToSpeech(mTestSpansihString, getApplicationContext(), this);
        //mStringToSpeech.setTargetLanguage("spanish");
        TwitterDataSingelton twitterDataSingelton = TwitterDataSingelton.getInstance();
        twitterDataSingelton.setContext(getApplicationContext());
        twitterDataSingelton.downloadTrendingCountries();
        twitterDataSingelton.downloadSentiment();
        twitterDataSingelton.downloadSentiment();

        TwitterTrendingTopic.Sentiment tmpSentiment =twitterDataSingelton.getSentimentForCountry("United States");
        if(mFaceView != null && tmpSentiment != null) {
            if (tmpSentiment.equals(TwitterTrendingTopic.Sentiment.Negative)) {
                Drawable tmpDrawable = getResources().getDrawable(R.drawable.mehhh50);
                mFaceView.setBackground(tmpDrawable);
            } else if (tmpSentiment.equals(TwitterTrendingTopic.Sentiment.Positive)) {
                Drawable tmpDrawable = getResources().getDrawable(R.drawable.happy50);
                mFaceView.setBackground(tmpDrawable);
            }
            if (tmpSentiment.equals(TwitterTrendingTopic.Sentiment.Neutral)) {
                Drawable tmpDrawable = getResources().getDrawable(R.drawable.sad50);
                mFaceView.setBackground(tmpDrawable);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onPickButtonClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            Context context = getApplicationContext();
            startActivityForResult(builder.build(context), REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getApplicationContext());
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
    public Location getLastLocation()
    {
        return mLastLocation;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_mobile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSpeechEngineReady() {
        //the speech engine is ready to speak
        //make it so
        mStringToSpeech.speakText();
    }

    @Override
    public void onEarthFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPlacesFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //
            // Toast.makeText(getApplicationContext(), "Location Recieved", Toast.LENGTH_SHORT).show();
            if(mLastLocation != null) {
                LatLng tmpLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(tmpLoc)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(getApplicationContext(), "Location onConnectionFailed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //move the map to our current position
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        if(mLastLocation != null) {
            LatLng tmpLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(tmpLoc)      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_activity_mobile, container, false);
            mHelloWorld = (TextView) rootView.findViewById(R.id.helloworldtextview);
            return rootView;
        }

    }
}
