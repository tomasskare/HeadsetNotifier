package org.nocrew.tomas.headsetnotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HeadsetNotifierBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs =
    		    PreferenceManager.getDefaultSharedPreferences(context);
   		boolean doAutostart = prefs.getBoolean("pref_boot", true);
   		if(!doAutostart) {
   			return;
   		}
   		
   		Intent serviceIntent = new Intent();
        serviceIntent.setClassName(context.getPackageName(), RegistrationService.class.getName());
        context.startService(serviceIntent);
	}

}
