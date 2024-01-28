package alam.sos.sosalam.JobSedulerActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alam.sos.sosalam.AlamActivity.AlarmReceiver;
import alam.sos.sosalam.AlamActivity.AlarmSoundService;
import alam.sos.sosalam.AlamActivity.MyBroadcastReceiver;
import alam.sos.sosalam.DatabaseHelper;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;
import alam.sos.sosalam.ShowAlamActivity;

public class MyJobIntentService extends JobIntentService {
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 133;
    String imeiSIM1,imeiSIM2;
    DatabaseReference databaseReference1;
    ArrayList<RegisterSetGet> adsList;
    String flag;
    String flaggg ="0";
    String flagg ="1";
    String FLAGID = "0";
    String FLAGIDD;
    private DatabaseHelper db;
    final Handler mHandler = new Handler();
    private static final String TAG = "SOSJobIntentService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 2;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MyJobIntentService.class, JOB_ID, intent);
        Log.e("enqueueWork","enqueueWork");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Job Execution Started");
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ALARM_REQUEST_CODE, alarmIntent, 0);
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
            Log.e("IMEISHOW", Id+" " +imeiSIM1+" "+imeiSIM2);
        }

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int maxCount = intent.getIntExtra("maxCountValue", -1);
        for (int i = 0; i < maxCount; i++) {
            Log.d(TAG, "onHandleWork: The number is: " + i);
            showToast("onHandleWork: The number is");
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
                            //updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                            Cursor re = db.getFlag();
                            if(re.getCount() == 0) {
                                return;
                            }
                            StringBuffer buffe = new StringBuffer();
                            while (re.moveToNext()) {
                                String Id =  re.getString(0);
                                FLAGIDD =  re.getString(1);
                                Log.e("FLGG", Id+" "+FLAGIDD);
                            }
                            if (FLAGID.equals(FLAGIDD)) {
                                setAlarm();
                                Log.e("Act", "Active User"+" "+flag);
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
                                Log.e("FLGG", Id+" "+FLAGIDD);
                            }
                            if (FLAGID.equals(FLAGIDD)) {
                                //setAlarm();
                                Log.e("Act", "Active User"+" "+flag);
                            }
                        } else {

                        }
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public void setAlarm() {
        Intent myIntent = new Intent(getApplicationContext(), ShowAlamActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Job Execution Finished");
    }


    // Helper for showing tests
    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
              // Toast.makeText(MyJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}