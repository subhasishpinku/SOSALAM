package alam.sos.sosalam.AlamActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import alam.sos.sosalam.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.alarm_sound);
        mp.start();
        Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show();
    }
}