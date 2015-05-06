package com.shyward.hellowatson;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EarthFragment.OnEarthFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EarthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EarthFragment extends Fragment {
    private static final String TAG = EarthFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnEarthFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EarthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EarthFragment newInstance(String param1, String param2) {
        EarthFragment fragment = new EarthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EarthFragment() {
        // Required empty public constructor
    }


    //public void set

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earth, container, false);
        // Inflate the layout for this fragment

        ///TODO: add query string values for sentiment for each country
        // http://ec2-52-24-137-228.us-west-2.compute.amazonaws.com/index.html?us=2&in=2&ru=3&jp=4&pk=5&uk=6&fr=7&mx=8&ca=9&ru=10&au=5&br=5&sa=10&ge=10

        String url = "http://ec2-52-24-137-228.us-west-2.compute.amazonaws.com/index.html?us=2&in=2&ru=3&jp=4&pk=5&uk=6&fr=7&mx=8&ca=9&ru=10&au=5&br=5&sa=10&ge=10";
        WebView myWebView = (WebView) view.findViewById(R.id.earth_fragment_webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);


        /*
        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, "onTouch");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        */

        //myWebView.addJavascriptInterface(new WebViewJavascriptInterface(App.context), "Android");
        myWebView.loadUrl(url);

        return view;

    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onEarthFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEarthFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEarthFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnEarthFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onEarthFragmentInteraction(Uri uri);
    }

}
