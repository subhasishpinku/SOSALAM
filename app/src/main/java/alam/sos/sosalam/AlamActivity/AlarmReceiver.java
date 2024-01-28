package alam.sos.sosalam.AlamActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//          //  context.startForegroundService(intent);
//            context.startService(new Intent(context, AlarmSoundService.class));
//        }
//        else {
//           // context.startService(intent);
//        }

        //Stop sound service to play sound for alarm
        //This will send a notification message and show notification in notification tray
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                AlarmNotificationService.class.getName());
//        startWakefulService(context, (intent.setComponent(comp)));

         context.startService(new Intent(context, AlarmSoundService.class));
     //   ComponentName compp = new ComponentName(context.getPackageName(), AlarmSoundService.class.getName());
       // ContextCompat.startForegroundService(context,intent.setComponent(compp));

    }


}
