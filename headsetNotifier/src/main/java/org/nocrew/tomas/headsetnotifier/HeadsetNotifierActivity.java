package org.nocrew.tomas.headsetnotifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.nocrew.tomas.headsetnotifier.R;

public class HeadsetNotifierActivity extends PreferenceActivity {
	private SharedPrefListener prefListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startMyService();

        addPreferencesFromResource(R.xml.preferences);

		SharedPreferences prefs =
    		    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefListener = new SharedPrefListener();
		prefListener.activityContext = getApplicationContext();
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	//    	Log.d("HeadsetNotifier", "onListItemClick called");
    }
    
    private void startMyService() {
    	Context context = getApplicationContext();
    	
        Intent serviceIntent = new Intent();
        serviceIntent.setClassName(context.getPackageName(), RegistrationService.class.getName());
        context.startService(serviceIntent);
    }
}
