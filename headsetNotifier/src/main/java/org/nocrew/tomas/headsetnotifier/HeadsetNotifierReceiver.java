package org.nocrew.tomas.headsetnotifier;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class HeadsetNotifierReceiver extends BroadcastReceiver {

	public boolean ignoreNext = false;
	
	private static HeadsetNotifierReceiver instance;
	private Notification notification;
	private int currState = 0;
	private int currMicrophone = 0;
	private boolean currFromIntent = false;
	
	private static final int NOTICE_ID = 1;

    public HeadsetNotifierReceiver() {
		super();
	}

    public static HeadsetNotifierReceiver getInstance() {
        if(instance == null) {
            instance = new HeadsetNotifierReceiver();
        }
        return instance;
    }

    public void handleNotification(Context context, int state, int microphone, boolean fromIntent) {
		
	//		Log.d("HeadsetNotifier", "handle, curr_state=" + currState + ", curr_mic=" + currMicrophone + ", state=" + state + ", microphone=" + microphone);

		SharedPreferences prefs =
    		    PreferenceManager.getDefaultSharedPreferences(context);
   		boolean isEnabled = prefs.getBoolean("pref_enable", true);
   		boolean isScreenEnabled = prefs.getBoolean("pref_screen", false);
   		String defType = prefs.getString("pref_deftype", "mic");

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		if(!isEnabled) {
			if(notification != null) {
				notificationManager.cancel(NOTICE_ID);
				notification = null;
			}
			currState = 0;
			currMicrophone = 0;
			currFromIntent = false;
			return;
		}

		if(ignoreNext) {
			ignoreNext = false;
			return;
		}

		if(currFromIntent && !fromIntent) {
			return;
		}

		if(!currFromIntent && !fromIntent) {
			if(defType.equals("mic")) {
				microphone = 1;
			} else {
				microphone = 0;
			}
		}
		
		if(currState == state && currMicrophone == microphone) {
			return;
		}
		
		currState = state;
		currMicrophone = microphone;
		currFromIntent = fromIntent;
		
		if(state == 1) {
			int icon;
			CharSequence contentTitle = context.getString(R.string.app_name);
			CharSequence contentText;

			if(microphone == 1) {
				icon = R.drawable.hs_icon_mic;
				contentText = context.getString(R.string.hs_mic_in);
			} else {
				icon = R.drawable.hs_icon_nomic;
				contentText = context.getString(R.string.hs_nomic_in);
			}

			if(notification != null) {
			    notificationManager.cancel(NOTICE_ID);
			    notification = null;
			}
			
			Intent notificationIntent = new Intent(context, HeadsetNotifierActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setContentTitle(contentTitle);
			builder.setContentText(contentText);
			builder.setSmallIcon(icon);
			builder.setContentIntent(contentIntent);
			builder.setAutoCancel(false);
			builder.setOngoing(true);
			notification = builder.build();

			notificationManager.notify(NOTICE_ID, notification);
			
			if(isScreenEnabled && fromIntent) {
				PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
				PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, context.getString(R.string.app_name));
				wl.acquire(5000);
			}
			
		} else if(notification != null) {
		    notificationManager.cancel(NOTICE_ID);
		    notification = null;
			currState = 0;
			currMicrophone = 0;
			currFromIntent = false;
		}
    }
    
    public void setCurrentHeadsetState(Context context) {
    	AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	int state = 0;
    	int microphone = 0;

    	if(am.isWiredHeadsetOn()) {
    		state = 1;
    	}
    	
    	handleNotification(context, state, microphone, false);
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
	    //		Log.d("HeadsetNotifier", "got intent, action=" + intent.getAction());
		
		if(!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			return;
		}
		
		int state = intent.getIntExtra("state", 0);
		int microphone = intent.getIntExtra("microphone", 0);

		handleNotification(context, state, microphone, true);
	}

}
