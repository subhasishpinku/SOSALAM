package alam.sos.sosalam.AlamActivity;//package com.example.sos.AlamActivity;
//
//import android.app.IntentService;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//
//
//import androidx.core.app.NotificationCompat;
//
//import com.example.sos.R;
//
//
//
///**
// * Created by sonu on 10/04/17.
// */
//
//public class AlarmNotificationService extends IntentService {
//    private NotificationManager alarmNotificationManager;
//
//    //Notification ID for Alarm
//    public static final int NOTIFICATION_ID = 1;
//
//    public AlarmNotificationService() {
//        super("AlarmNotificationService");
//    }
//
//    @Override
//    public void onHandleIntent(Intent intent) {
//
//        //Send notification
//        sendNotification("Wake Up! Wake Up! Alarm started!!");
//    }
//
//    //handle notification
//    private void sendNotification(String msg) {
//        alarmNotificationManager = (NotificationManager) this
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //get pending intent
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, YourService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //Create notification
//        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
//                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setContentText(msg).setAutoCancel(true);
//        alamNotificationBuilder.setContentIntent(contentIntent);
//
//        //notiy notification manager about new notification
//        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());
//    }
//
//
//}
