package alam.sos.sosalam.AlamActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import alam.sos.sosalam.DatabaseHelper;
import alam.sos.sosalam.ImeiTelephone.TelephonyInfo;
import alam.sos.sosalam.JobSedulerActivity.YourServiceRestarterBroadcastReceiver;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;
import alam.sos.sosalam.ShowAlamActivity;

public class YourService extends Service {
    public int counter=0;
    public YourService(Context applicationContext) {
        super();
        Log.e("HERE", "here I am!");
    }
    public YourService() {
    }
    public static String str_receiver = "com.bracesmedia.tracking";
    Intent intent;
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 133;
    static final Integer PHONESTATS = 0x1;
    String imei,androidid;
    private int checkedPermission = PackageManager.PERMISSION_DENIED;
    public static boolean isMultiSimEnabled = false;
    public static List<SubscriptionInfo> subInfoList;
    public static final int REQUEST_CODE_PHONE_STATE_READ = 100;
    private SubscriptionManager subscriptionManager;
    public static ArrayList<String> numbers;
    String imeiSIM1,imeiSIM2;
    DatabaseReference databaseReference1;
    ArrayList<RegisterSetGet> adsList;
    String flag;
    String flaggg ="0";
    String flagg ="1";
    private DatabaseHelper db;
    String FLAGID = "0";
    String FLAGIDD;
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    Alarm alarm = new Alarm();
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    public void onCreate()
    {
        super.onCreate();
        Log.e("STARTSERVICE","START");
        intent = new Intent(str_receiver);
        Intent alarmIntent = new Intent(YourService.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(YourService.this, ALARM_REQUEST_CODE, alarmIntent, 0);
        askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        numbers = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(getApplicationContext());
        }
        FirebaseApp.initializeApp(this);
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        db = new DatabaseHelper(this);
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
            Log.e("IMEISHOW1", Id+" " +imeiSIM1+" "+imeiSIM2);
        }
        startTimer();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //alarm.setAlarm(this);
       // setAlarm();
        Log.e("STARTSERVICE","START");
        startTimer();
        return START_STICKY;
    }




    public void stopAlarmManager() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent
        stopService(new Intent(getApplicationContext(), AlarmSoundService.class));
    }
    public void startAlert() {
        int timeInSec = 2;
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (timeInSec * 1000), pendingIntent);

    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getApplicationContext(), permission)) {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{permission}, requestCode);
            }
        } else {
            imei = getImeiNumber();
            Log.e("IMEII",imei);
            getClientPhoneNumber();
            androidid=getAndroidId();
         //   Toast.makeText(this,permission + " is already granted.", Toast.LENGTH_SHORT).show();
            AllEmai();
        }
    }
    private void AllEmai(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        imeiSIM1 = telephonyInfo.getImsiSIM1();
        imeiSIM2 = telephonyInfo.getImsiSIM2();
        imei = imeiSIM1+","+imeiSIM2;
        Log.e("IMEI",imei);
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
        boolean isDualSIM = telephonyInfo.isDualSIM();
        //    Toast.makeText(getApplicationContext(),"imei"+" "+imeiSIM1+ " "+"IME2"+imeiSIM2,Toast.LENGTH_SHORT).show();
        Log.e("device"," IME1:"+imeiSIM1 + "" + " IME2 : " + imeiSIM2 + "" + " IS DUAL SIM : " + imeiSIM2 + "" + " IS SIM1 READY : " + isSIM1Ready + "" + " IS SIM2 READY : " + isSIM2Ready);

    }
    @SuppressLint("MissingPermission")
    private String getImeiNumber() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imei = telephonyManager.getImei();
            if (imei!=null){
                return telephonyManager.getImei();
            }else {
                return null;
            }


        }
        else {
            return telephonyManager.getDeviceId();
        }
    }
    @SuppressLint({"NewApi", "MissingPermission"})
    private void getClientPhoneNumber() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                subInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            }
            if (subInfoList.size() > 1)
            {
                isMultiSimEnabled = true;
            }
            for (SubscriptionInfo subscriptionInfo : subInfoList)
            {
                numbers.add(subscriptionInfo.getNumber());
            }
        }catch (Exception e)
        {
        }
    }
    private String getAndroidId() {
        androidid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("TAG",Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ALLOWED_GEOLOCATION_ORIGINS));
        Log.e("TAG",Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));
        return androidid;
    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.e("in timer", "in timer ++++  "+ (counter++));
                StartAlam();
            }
        };
    }
   public void StartAlam() {
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
                       //updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                       Cursor re = db.getFlag();
                       if(re.getCount() == 0) {
                           return;
                       }
                       StringBuffer buffe = new StringBuffer();
                       while (re.moveToNext()) {
                           String Id =  re.getString(0);
                           FLAGIDD =  re.getString(1);
                           Log.e("FLG", Id+" "+FLAGIDD);
                       }
                       if (FLAGID.equals(FLAGIDD)) {
                           Intent myIntent = new Intent(getApplicationContext(), ShowAlamActivity.class);
                           myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(myIntent);
                           Log.e("AUser", " "+flag+" "+FLAGIDD);
                       }
                   } else {

                   }
               }
           }
           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
       databaseReference1.child("Register_User").orderByChild("emaino2")
               .equalTo(imeiSIM2).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               final List<String> name = new ArrayList<String>();
               adsList = new ArrayList<RegisterSetGet>();
               for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                   flag = String.valueOf(areaSnapshot.child("flag").getValue());
                   adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                   if (flag.equals(flagg)){
                       //updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                       Cursor re = db.getFlag();
                       if(re.getCount() == 0) {
                           return;
                       }
                       StringBuffer buffe = new StringBuffer();
                       while (re.moveToNext()) {
                           String Id =  re.getString(0);
                           FLAGIDD =  re.getString(1);
                           Log.e("FLG", Id+" "+FLAGIDD);
                       }
                       if (FLAGID.equals(FLAGIDD)) {
                           Intent myIntent = new Intent(getApplicationContext(), ShowAlamActivity.class);
                           myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(myIntent);
                           Log.e("AUser1", " "+flag+" "+FLAGIDD);
                       }
                   } else {

                   }
               }

           }
           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }
    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Override
    public void onStart(Intent intent, int startId)
    {
        //alarm.setAlarm(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
       // alarm.cancelAlarm(this);
        Intent broadcastIntent = new Intent(this, YourServiceRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }
}

