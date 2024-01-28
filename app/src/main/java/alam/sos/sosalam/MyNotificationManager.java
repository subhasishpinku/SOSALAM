package alam.sos.sosalam;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import alam.sos.sosalam.AlamActivity.AlarmReceiver;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;

public class MyNotificationManager {
    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;
    private Context mCtx;
    private DatabaseHelper db;
    DatabaseReference databaseReference1;
    ArrayList<RegisterSetGet> adsList;
    String flag;
    String flaggg ="0";
    String flagg ="1";
    String FLAGID = "0";
    String FLAGIDD;
    String imeiSIM1,imeiSIM2;
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 133;
    private MediaPlayer player;
    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
        db = new DatabaseHelper(this.mCtx);
        FirebaseApp.initializeApp(this.mCtx);
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        Intent alarmIntent = new Intent(mCtx, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mCtx, ALARM_REQUEST_CODE, alarmIntent, 0);
    }
    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, App.CH1);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.messageicon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.messageicon)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.messageicon))
                .setContentText(message)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +mCtx.getPackageName()+"/"+ R.raw.alarm_sound))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400,
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400,
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400 })
                .setColor(Color.BLUE)
                .build();
        try {
            Uri uri = Uri.parse("android.resource://" + mCtx.getPackageName() + "/" + R.raw.alarm_sound);
            player = MediaPlayer.create(mCtx, uri);
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
        if (message.isEmpty()){
            Log.e("OK","OK3");
        }
        else {
            Cursor re = db.getFlagData();
            if(re.getCount() == 0) {
                Log.e("Error","Nothing found");
                return;
            }
            StringBuffer buffe = new StringBuffer();
            while (re.moveToNext()) {
                String Id =  re.getString(0);
                imeiSIM1 =  re.getString(1);
                imeiSIM2 = re.getString(2);
                Log.e("IMEISHOW", Id+" " +imeiSIM1+" "+imeiSIM2);
            }
            databaseReference1.child("Register_User").orderByChild("emaino1")
                    .equalTo(imeiSIM1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<String> name = new ArrayList<String>();
                    adsList = new ArrayList<RegisterSetGet>();
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        flag = String.valueOf(areaSnapshot.child("flag").getValue());
                        adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                        if (flag.equals(flagg)){
                            setAlarm();
                            Intent intent1 = new Intent(mCtx, ShowAlamActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent1);
                        }
                        else {

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, App.CH2);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.drawable.messageicon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.messageicon)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.messageicon))
                .setContentText(message)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +mCtx.getPackageName()+"/"+ R.raw.alarm_sound))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400,
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400,
                        300, 400, 300, 400, 300, 400, 300, 400, 300, 400, 300, 400 })
                .setColor(Color.BLUE)
                .build();
        Log.e("FFCM"," " +title+" "+" "+message+" ");
        try {
            Uri uri = Uri.parse("android.resource://" + mCtx.getPackageName() + "/" + R.raw.alarm_sound);
            player = MediaPlayer.create(mCtx, uri);
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        if (message.isEmpty()){
            Log.e("OK","OK1");
        }
        else {
            Log.e("OK","OK2");
            Cursor re = db.getFlagData();
            if(re.getCount() == 0) {
                Log.e("Error","Nothing found");
                return;
            }
            StringBuffer buffe = new StringBuffer();
            while (re.moveToNext()) {
                String Id =  re.getString(0);
                imeiSIM1 =  re.getString(1);
                imeiSIM2 = re.getString(2);
                Log.e("IMEISHOW", Id+" " +imeiSIM1+" "+imeiSIM2);
            }
            databaseReference1.child("Register_User").orderByChild("emaino1")
                    .equalTo(imeiSIM1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<String> name = new ArrayList<String>();
                    adsList = new ArrayList<RegisterSetGet>();
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        flag = String.valueOf(areaSnapshot.child("flag").getValue());
                        adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                        if (flag.equals(flagg)){
                            setAlarm();
                            Intent intent1 = new Intent(mCtx, ShowAlamActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent1);

                        }
                        else {

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    public void setAlarm() {
        int alarmTriggerTime = 0;
        AlarmManager manager = (AlarmManager) mCtx.getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmTriggerTime, pendingIntent);
    }
    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
