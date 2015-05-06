package com.shyward.hellowatson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	 private static String TAG = SplashActivity.class.getName();
	 private static long SLEEP_TIME = 4;    // Sleep for some time

	 
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		 super.onCreate(savedInstanceState);
	  
	     this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

         setContentView(R.layout.splash);
	}
	 
	@Override
	public void onResume()
	{
		super.onResume();
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
	}

	 private class IntentLauncher extends Thread {
	  @Override
	  /**
	   * Sleep for some time and than start new activity.
	   */
	  public void run() {
	     try {
	        // Sleeping
	        Thread.sleep(SLEEP_TIME*500);
	     } catch (Exception e) {
	    	 Log.e(TAG, e.getMessage());
	     }

        	//Intent newIntent = new Intent(getApplicationContext(), MapViewFragmentActivityMainScreen.class);
        	Intent newIntent = new Intent(getApplicationContext(), MainActivityMobile.class);
        	SplashActivity.this.startActivity(newIntent);
        	SplashActivity.this.finish();
	  }
	}
}


