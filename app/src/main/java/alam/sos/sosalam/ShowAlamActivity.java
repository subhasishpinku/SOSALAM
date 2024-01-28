package alam.sos.sosalam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alam.sos.sosalam.AlamActivity.AlarmReceiver;
import alam.sos.sosalam.AlamActivity.AlarmSoundService;
import alam.sos.sosalam.ImeiTelephone.TelephonyInfo;
import alam.sos.sosalam.JobSedulerActivity.MyJobIntentService;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;
import alam.sos.sosalam.SetgetActivity.usaralamDataShowUpdate;

public class ShowAlamActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ShowAlam";
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 133;
    Toolbar toolbar;
    LinearLayout saveId;
    ProgressDialog progressDialog ;
    static final Integer PHONESTATS = 0x1;
    String imei,androidid;
    private int checkedPermission = PackageManager.PERMISSION_DENIED;
    public static boolean isMultiSimEnabled = false;
    public static List<SubscriptionInfo> subInfoList;
    public static final int REQUEST_CODE_PHONE_STATE_READ = 100;
    private SubscriptionManager subscriptionManager;
    public static ArrayList<String> numbers;
    String imeiSIM1,imeiSIM2;
    Intent myIntent = null;
    DatabaseReference databaseReference1;
    String backupId,text,dateIdd,timeIdd,Imageulr;
    ArrayList<usaralamDataShowUpdate> List;
    ImageView ImgeID;
    TextView textID,datename,timename;
    ArrayList<RegisterSetGet> adsList;
    String backuupID,dsignation,emaino1,emaino2,flag,imgurl,phonenumber,username,whatsappno;
    String flagg ="1";
    String flaggg ="0";
    List<ApplicationInfo> packages;
    PackageManager pm;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock wakeLock;
    DatabaseHelper helper;
    String updateflag = "1";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alam);
        initToolBar();
        textID = (TextView)findViewById(R.id.textID);
        datename = (TextView)findViewById(R.id.datename);
        timename = (TextView)findViewById(R.id.timename);
        ImgeID = (ImageView)findViewById(R.id.ImgeID);
        saveId =(LinearLayout) findViewById(R.id.saveId);
        saveId.setOnClickListener(this);
        progressDialog = new ProgressDialog(ShowAlamActivity.this);
      //  turnOnScreen();
        helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase dbb = helper.getReadableDatabase();
        dbb.execSQL("UPDATE tableflag SET tableflagg='1' WHERE tableflagid=" +updateflag);
        askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        numbers = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(ShowAlamActivity.this);
        }
        Intent alarmIntent = new Intent(ShowAlamActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ShowAlamActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);
//        myIntent = new Intent(ShowAlamActivity.this, YourService.class);
//        startService(myIntent);
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference1.child("usaralamDataShow").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> name = new ArrayList<String>();
                List = new ArrayList<usaralamDataShowUpdate>();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    backupId = String.valueOf(areaSnapshot.child("backupId").getValue());
                    text = String.valueOf(areaSnapshot.child("text").getValue());
                    dateIdd = String.valueOf(areaSnapshot.child("dateId").getValue());
                    timeIdd = String.valueOf(areaSnapshot.child("timeId").getValue());
                    Imageulr = String.valueOf(areaSnapshot.child("imageulr").getValue());
                    Log.e("USERALAM",backupId+" "+text+" "+dateIdd+" "+timeIdd+" "+Imageulr);
                    List.add(areaSnapshot.getValue(usaralamDataShowUpdate.class));
                    Glide.with(getApplicationContext())
                            .load(Imageulr)
                            .into(ImgeID);
                    textID.setText(text);
                    datename.setText(dateIdd);
                    timename.setText(timeIdd);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference1.child("Register_User").orderByChild("emaino1")
                    .equalTo(imeiSIM1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> name = new ArrayList<String>();
                adsList = new ArrayList<RegisterSetGet>();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    backuupID = String.valueOf(areaSnapshot.child("backuupID").getValue());
                    dsignation = String.valueOf(areaSnapshot.child("dsignation").getValue());
                    emaino1 = String.valueOf(areaSnapshot.child("emaino1").getValue());
                    emaino2 = String.valueOf(areaSnapshot.child("emaino2").getValue());
                    flag = String.valueOf(areaSnapshot.child("flag").getValue());
                    imgurl = String.valueOf(areaSnapshot.child("imgurl").getValue());
                    phonenumber = String.valueOf(areaSnapshot.child("phonenumber").getValue());
                    username = String.valueOf(areaSnapshot.child("username").getValue());
                    whatsappno = String.valueOf(areaSnapshot.child("whatsappno").getValue());
                    Log.e("userDatashow",backuupID+" "+dsignation+" "+emaino1+" "+emaino2+" "+flag+" "+imgurl+" "+phonenumber+" "+username+" "+whatsappno);
                    adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                    if (flag.equals(flagg)){
                        //  updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                        Log.e("Active", "Active User");
                        setAlarm();

                    }
                    else {
                        Log.e("Active", "No Active User");
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
                    backuupID = String.valueOf(areaSnapshot.child("backuupID").getValue());
                    dsignation = String.valueOf(areaSnapshot.child("dsignation").getValue());
                    emaino1 = String.valueOf(areaSnapshot.child("emaino1").getValue());
                    emaino2 = String.valueOf(areaSnapshot.child("emaino2").getValue());
                    flag = String.valueOf(areaSnapshot.child("flag").getValue());
                    imgurl = String.valueOf(areaSnapshot.child("imgurl").getValue());
                    phonenumber = String.valueOf(areaSnapshot.child("phonenumber").getValue());
                    username = String.valueOf(areaSnapshot.child("username").getValue());
                    whatsappno = String.valueOf(areaSnapshot.child("whatsappno").getValue());
                    Log.e("userDatashow",backuupID+" "+dsignation+" "+emaino1+" "+emaino2+" "+flag+" "+imgurl+" "+phonenumber+" "+username+" "+whatsappno);
                    adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                    if (flag.equals(flagg)){
                        //  updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                        Log.e("Active", "Active User");
                        setAlarm();

                    }
                    else {
                        Log.e("Active", "No Active User");
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        pm = getPackageManager();
//        packages = pm.getInstalledApplications(0);
//        ActivityManager mActivityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ApplicationInfo packageInfo : packages) {
//            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
//            if(packageInfo.packageName.equals("com.example.sos")) continue;
//            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
//        }
//        Intent mIntent = new Intent(this, MyJobIntentService.class);
//        mIntent.putExtra("maxCountValue", 1000);
//        MyJobIntentService.enqueueWork(this, mIntent);

        Intent mIntent = new Intent(this, MyJobIntentService.class);
        mIntent.putExtra("maxCountValue", 1000);
        MyJobIntentService.enqueueWork(this, mIntent);
    }
    public void setAlarm() {
        int alarmTriggerTime = 0;
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.SECOND, alarmTriggerTime);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmTriggerTime, pendingIntent);
    }

    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Alam");
        setSupportActionBar(toolbar);
    }
    public void turnOnScreen(){
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "myapp:WAKE_LOCK_TAG");
        wakeLock.acquire();
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ShowAlamActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ShowAlamActivity.this, permission)) {
                ActivityCompat.requestPermissions(ShowAlamActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(ShowAlamActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = getImeiNumber();
            Log.e("IMEII",imei);
            getClientPhoneNumber();
            androidid=getAndroidId();
            Toast.makeText(this,permission + " is already granted.", Toast.LENGTH_SHORT).show();
            AllEmai();
//          checkServiceStatus();
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
            return telephonyManager.getImei();
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
            Log.e(TAG,"Sim 1:- "+numbers.get(0));
            Log.e(TAG,"Sim 2:- "+ numbers.get(1));
        }catch (Exception e)
        {
            Log.d(TAG,e.toString());
        }
    }
    private String getAndroidId() {
        androidid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("TAG",Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ALLOWED_GEOLOCATION_ORIGINS));
        Log.e("TAG",Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));
        return androidid;
    }
    @SuppressLint("NewApi")
    private void requestPermission() {
        Toast.makeText(ShowAlamActivity.this, "Requesting permission", Toast.LENGTH_SHORT).show();
        this.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE_PHONE_STATE_READ);
    }
    @SuppressLint("MissingPermission")
    public void showDeviceInfo() {
        TelephonyManager manager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        StringBuilder stringBuilder = new StringBuilder();
        if (checkedPermission != PackageManager.PERMISSION_DENIED) {
            stringBuilder.append("Board : " + Build.BOARD + "\n");
            stringBuilder.append("Brand : " + Build.BRAND + "\n");
            stringBuilder.append("DEVICE : " + Build.DEVICE + "\n");
            stringBuilder.append("Display : " + Build.DISPLAY + "\n");
            stringBuilder.append("FINGERPRINT : " + Build.FINGERPRINT + "\n");
            stringBuilder.append("HARDWARE : " + Build.HARDWARE + "\n");
            stringBuilder.append("ID : " + Build.ID + "\n");
            stringBuilder.append("Manufacturer : " + Build.MANUFACTURER + "\n");
            stringBuilder.append("MODEL : " + Build.MODEL + "\n");
            stringBuilder.append("SERIAL : " + Build.SERIAL + "\n");
            stringBuilder.append("VERSION : " + Build.VERSION.SDK_INT + "\n");
            stringBuilder.append("Line 1 : " + manager.getLine1Number() + "\n");
            stringBuilder.append("Device ID/IMEI : " + manager.getDeviceId() + "\n");
            Log.e("Imeii", " "+ manager.getDeviceId());
            stringBuilder.append("IMSI : " + manager.getSubscriberId());
        } else {
            stringBuilder.append("Can't access device info !");
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveId:
                updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
                helper = new DatabaseHelper(getApplicationContext());
                SQLiteDatabase dbb = helper.getReadableDatabase();
                dbb.execSQL("UPDATE tableflag SET tableflagg='0' WHERE tableflagid=" +updateflag);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                break;
                default:
        }
    }

    public void updateEvent(final String userId,final String userName,final String disg,final String userPhone,final String userWhatsapp,final String userFlag,final String Imei1,final String Imei2,final String imag) {
        RegisterSetGet classifiedAd = createClassifiedAdObj(userId,userName,disg,userPhone,userWhatsapp,userFlag,Imei1,Imei2,imag);
        updateClassifiedToDB(classifiedAd,userId);
    }
    private RegisterSetGet createClassifiedAdObj(final String userId, final String userName, final String disg, final String userPhone, final String userWhatsapp, final String userFlag, final String Imei1, final String Imei2, final String imag) {
        final RegisterSetGet ad = new RegisterSetGet();
        Log.e("ALERT", "T3" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
        ad.setBackuupID(userId);
        ad.setUsername(userName);
        ad.setDsignation(disg);
        ad.setPhonenumber(userPhone);
        ad.setWhatsappno(userWhatsapp);
        ad.setFlag(userFlag);
        ad.setEmaino1(Imei1);
        ad.setEmaino2(Imei2);
        ad.setImgurl(imag);
        return ad;
    }
    private void updateClassifiedToDB(RegisterSetGet classifiedAd, String userId) {
        addClassified(classifiedAd, userId);
        Log.e("Register_User",userId);
    }
    private void addClassified(RegisterSetGet classifiedAd, String userId) {
        classifiedAd.setBackuupID(userId);
        databaseReference1.child("Register_User").child(userId)
                .setValue(classifiedAd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            stopAlarmManager();
                        } else {

                        }
                    }
                });
    }

    public void stopAlarmManager() {
        AlarmManager manager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent
        stopService(new Intent(getApplicationContext(), AlarmSoundService.class));
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("Inside onResume");
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("Inside onStart");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("Inside onReStart");
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("Inside onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("Inside onStop");
    }

    @Override
    public void onDestroy(){
        updateEvent(backuupID,username,dsignation,phonenumber,whatsappno,flaggg,emaino1,emaino2,imgurl);
        helper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase dbb = helper.getReadableDatabase();
        dbb.execSQL("UPDATE tableflag SET tableflagg='0' WHERE tableflagid=" +updateflag);
        super.onDestroy();
        System.out.println("Inside onDestroy");
    }


}