package com.shyward.hellowatson;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlacesFragment.OnPlacesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacesFragment extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static int REQUEST_PLACE_PICKER = 101;
    public MapFragment mMapView;
    public Location mLocation;
    public GoogleMap map;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnPlacesFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlacesFragment newInstance(String param1, String param2) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_places);

        android.support.v4.app.FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.mapview);


        GoogleMap myMap;

        myMap = mySupportMapFragment.getMap();
        if (myMap != null)
            myMap.setMyLocationEnabled(true);

        //onPickButtonClick();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPlacesFragmentInteraction(uri);
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //Toast.makeText(getApplicationContext(), "Location Recieved", Toast.LENGTH_SHORT).show();

            // Gets to GoogleMap from the MapView and does initialization stuff
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls

            MapsInitializer.initialize(this);

            LatLng tmpLoc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(tmpLoc, 10);
            map.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPlacesFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onPlacesFragmentInteraction(Uri uri);
    }

}
