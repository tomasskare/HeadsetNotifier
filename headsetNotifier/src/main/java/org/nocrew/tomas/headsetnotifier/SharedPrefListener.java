package org.nocrew.tomas.headsetnotifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SharedPrefListener implements OnSharedPreferenceChangeListener {
	public Context activityContext;
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	   	HeadsetNotifierReceiver receiver = HeadsetNotifierReceiver.getInstance();
    	if(receiver != null) {
    		receiver.setCurrentHeadsetState(activityContext);
    	}
	}
}
