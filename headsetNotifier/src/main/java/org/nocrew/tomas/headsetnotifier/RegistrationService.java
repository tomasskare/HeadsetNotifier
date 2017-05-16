package org.nocrew.tomas.headsetnotifier;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class RegistrationService extends Service {
    private HeadsetNotifierReceiver receiver;

    @Override
    public void onCreate()
    {
        super.onCreate();
        
    	receiver = HeadsetNotifierReceiver.getInstance();

    	Intent previousIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        if(previousIntent != null) {
        	receiver.ignoreNext = true;
        }
        	
       	registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

   		Context context = getApplicationContext();
   		receiver.setCurrentHeadsetState(context);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(receiver != null) {
        	unregisterReceiver(receiver);
        }
    }
}
