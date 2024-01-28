package alam.sos.sosalam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alam.sos.sosalam.AlamActivity.AlarmReceiver;
import alam.sos.sosalam.AlamActivity.AlarmSoundService;
import alam.sos.sosalam.AlamActivity.YourService;
import alam.sos.sosalam.ImeiTelephone.TelephonyInfo;
import alam.sos.sosalam.JobSedulerActivity.MyJobIntentService;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;
import alam.sos.sosalam.SetgetActivity.usaralamDataShowUpdate;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView textview_marquee;
    CardView desID,empID,imgID,msgID,imeiID,alertID;
    Intent myIntent = null;
    String flagg ="1";
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
    ArrayList<usaralamDataShowUpdate> List;
    ArrayList<RegisterSetGet> adsList;
    String flag;
    List<ApplicationInfo> packages;
    PackageManager pm;
    SharedPreferences sp;
    private DatabaseHelper db;
    private PendingIntent pendingIntent;
    String FLAGID = "0";
    String FLAGIDD;
    Intent mServiceIntent;
    private YourService mSensorService;
    Context ctx;
    private ProgressDialog progressDialog;
    final Handler mHandler = new Handler();
    public Context getCtx() {
        return ctx;
    }
    String type = "O";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        textview_marquee = (TextView) this.findViewById(R.id.textview_marquee);
        textview_marquee.setText("SOS EMPOLOYEE APPLICATION CITY ALO");
        textview_marquee.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textview_marquee.setMarqueeRepeatLimit(-1);
        textview_marquee.setHorizontallyScrolling(true);
        textview_marquee.setFocusable(true);
        textview_marquee.setFocusableInTouchMode(true);
        textview_marquee.setSelected(true);
        initToolBar();
        desID = (CardView)findViewById(R.id.desID);
        empID = (CardView)findViewById(R.id.empID);
        imgID = (CardView)findViewById(R.id.imgID);
        msgID = (CardView)findViewById(R.id.msgID);
        imeiID = (CardView)findViewById(R.id.imeiID);
        alertID = (CardView)findViewById(R.id.alertID);
        desID.setOnClickListener(this);
        empID.setOnClickListener(this);
        imgID.setOnClickListener(this);
        msgID.setOnClickListener(this);
        imeiID.setOnClickListener(this);
        alertID.setOnClickListener(this);
        if (type.equals("O")){
            empID.setVisibility(View.VISIBLE);
        }
//        myIntent = new Intent(HomeActivity.this, YourService.class);
//        startService(myIntent);
        Intent alarmIntent = new Intent(HomeActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);
        db = new DatabaseHelper(this);
        askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        numbers = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(getApplicationContext());
        }
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference1.child("Register_User").orderByChild("emaino1")
                .equalTo(imeiSIM1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> name = new ArrayList<String>();
                adsList = new ArrayList<RegisterSetGet>();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    flag = String.valueOf(areaSnapshot.child("flag").getValue());
                    Log.e("FLAG",flag);
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
                            StartService();
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
                    Log.e("FLAG",flag);
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
                            StartService();

                        }
                    } else {

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Cursor res = db.getFlagData();
        if(res.getCount() == 0) {
            finish();
            startActivity(getIntent());
            return;
        }
        else {

        }
 //        pm = getPackageManager();
//        packages = pm.getInstalledApplications(0);
//        ActivityManager mActivityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ApplicationInfo packageInfo : packages) {
//            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
//            if(packageInfo.packageName.equals("com.example.sos")) continue;
//            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
//        }
        Intent mIntent = new Intent(this, MyJobIntentService.class);
        mIntent.putExtra("maxCountValue", 1000);
       // mIntent.putExtra("maxCountValue", 31104000);
        MyJobIntentService.enqueueWork(this, mIntent);
        ctx = this;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSensorService = new YourService(getCtx());
                mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
                if (!isMyServiceRunning(mSensorService.getClass())) {
                    startService(mServiceIntent);
                }
            }
        });

    }

    public void stopAlarmManager() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent
        stopService(new Intent(getApplicationContext(), AlarmSoundService.class));
    }
    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Home");
        // setSupportActionBar(toolbar);
      //  toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
//        toolbar.setNavigationOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                }
//        );
    }

    public void StartService(){
//        Intent serviceIntent = new Intent(this, YourService.class);
//        startService(serviceIntent);
        mSensorService = new YourService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.desID:
                Intent intent = new Intent(getApplicationContext(), DesignationActivity.class);
                startActivity(intent);
                break;
            case R.id.empID:
                Intent intent1 = new Intent(getApplicationContext(), EmpActivity.class);
                startActivity(intent1);
                break;
            case R.id.imgID:
                Intent intent2 = new Intent(getApplicationContext(), ShowAlamActivity.class);
                startActivity(intent2);
                break;
            case R.id.msgID:
                Intent intent3 = new Intent(getApplicationContext(), MsgLogActivity.class);
                startActivity(intent3);
                break;
            case R.id.imeiID:
                Intent intent4 = new Intent(getApplicationContext(), ImeinumberActivity.class);
                startActivity(intent4);
                break;
            case R.id.alertID:
                Intent intent5 = new Intent(getApplicationContext(), CreatealertActivity.class);
                startActivity(intent5);
                break;
            default:
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = getImeiNumber();
            Log.e("IMEII",imei);
            getClientPhoneNumber();
            androidid=getAndroidId();
            //Toast.makeText(this,permission + " is already granted.", Toast.LENGTH_SHORT).show();
            AllEmai();
        }
    }
    private void AllEmai(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        imeiSIM1 = telephonyInfo.getImsiSIM1();
        imeiSIM2 = telephonyInfo.getImsiSIM2();
        imei = imeiSIM1+","+imeiSIM2;
        Log.e("IMEI",imei);
        Cursor res = db.getFlagData();
        if(res.getCount() == 0) {
            Log.e("ErrorDDB","Nothing found");
            db.IMEIINSERT(imeiSIM1,imeiSIM2);
            db.FLAG(FLAGID);
            return;
        }
        else {

        }
        //sendToken(imeiSIM1,imeiSIM2);
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

    public void sendToken(final String imeiSIM1,final String imeiSIM2) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        final String token = SharedPreference.getInstance(this).getDeviceToken();
       // final String email = editTextEmail.getText().toString();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(HomeActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", imeiSIM1);
                params.put("token", token);
                Log.e("TOk"," "+token);
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart(){
        stopAlarmManager();
        super.onStart();
        Log.e("Inside onResume","onStart called when activity is becoming visible to the user");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("Inside onResume","onResume called when activity will start interacting with the user");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("Inside onResume","onPause called when activity is not visible to the user");
        Intent mIntent = new Intent(this, MyJobIntentService.class);
        mIntent.putExtra("maxCountValue", 1000);
        MyJobIntentService.enqueueWork(this, mIntent);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e("Inside onResume","onStop called when activity is no longer visible to the user");
        Intent mIntent = new Intent(this, MyJobIntentService.class);
        mIntent.putExtra("maxCountValue", 1000);
        MyJobIntentService.enqueueWork(this, mIntent);
    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.e("Inside onResume","onRestart called after your activity is stopped, prior to start");
       // finish();
       // startActivity(getIntent());

    }
    @Override
    public void onDestroy(){
//        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
        Log.e("Inside onResume","onDestroy called before the activity is destroyed");
    }
}
