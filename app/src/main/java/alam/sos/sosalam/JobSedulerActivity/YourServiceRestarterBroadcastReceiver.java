package alam.sos.sosalam.JobSedulerActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.ContextCompat;

import alam.sos.sosalam.AlamActivity.YourService;


/**
 * Created by fabio on 24/01/2016.
 */
public class YourServiceRestarterBroadcastReceiver extends BroadcastReceiver {
    final Handler mHandler = new Handler();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(YourServiceRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
       // context.startService(new Intent(context, YourService.class));;
        ComponentName compp = new ComponentName(context.getPackageName(), YourService.class.getName());
        ContextCompat.startForegroundService(context,intent.setComponent(compp));
    }

}
