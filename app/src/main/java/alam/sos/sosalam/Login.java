package alam.sos.sosalam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
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

import alam.sos.sosalam.AlamActivity.YourService;
import alam.sos.sosalam.ImeiTelephone.TelephonyInfo;
import alam.sos.sosalam.JobSedulerActivity.MyJobIntentService;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;

public class Login extends AppCompatActivity {
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
    EditText phID;
    Button LogInBtn;
    private String emaino1;
    private String emaino2;
    private ProgressDialog progressDialog;
    final Handler mHandler = new Handler();
    Context ctx;
    private YourService mSensorService;
    Intent mServiceIntent;
    TextView registrationId;
    public Context getCtx() {
        return ctx;
    }
    private static final int REQUEST_PHONE_STATE_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        LogInBtn =(Button)findViewById(R.id.LogInBtn);
        phID =(EditText) findViewById(R.id.phID);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            Intent mIntent = new Intent(getApplicationContext(), MyJobIntentService.class);
            mIntent.putExtra("maxCountValue", 1000);
            MyJobIntentService.enqueueWork(getApplicationContext(), mIntent);
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }
        FirebaseApp.initializeApp(this);
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        numbers = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(getApplicationContext());
        }
        Loginuser();
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
        registrationId = (TextView)findViewById(R.id.registrationId);
        registrationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),ImeiInstallerActivity.class);
                startActivity(intent);

            }
        });
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
    public void StartService(){
        mSensorService = new YourService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Login.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this, permission)) {
                ActivityCompat.requestPermissions(Login.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(Login.this, new String[]{permission}, requestCode);
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
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
        boolean isDualSIM = telephonyInfo.isDualSIM();
        Loginuser();
    }
    private String getImeiNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String imei = telephonyManager.getImei();
                if (imei != null) {
                    return imei;
                } else {
                    // If getImei() is null, you may use getDeviceId() as an alternative
                    return telephonyManager.getDeviceId();
                }
            }
        } else {
            // Request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_PERMISSION);
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    private String getImeiNumber1() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
    public void Loginuser(){
            LogInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emai = phID.getText().toString().trim();
                    if (TextUtils.isEmpty(emai)){
                        phID.setError("Enter Phone Number");
                        phID.requestFocus();
                        return;
                    }
                    askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
                    numbers = new ArrayList<String>();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subscriptionManager = SubscriptionManager.from(getApplicationContext());
                    }
                    userdata(phID.getText().toString().trim());
                }
            });
    }
    public void userdata(final String phone){
        FirebaseApp.initializeApp(this);
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference1.child("Register_User").orderByChild("phonenumber")
                .equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> name = new ArrayList<String>();
                adsList = new ArrayList<RegisterSetGet>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                        String flag = String.valueOf(areaSnapshot.child("flag").getValue());
                        String backuupID = String.valueOf(areaSnapshot.child("backuupID").getValue());
                        String dsignation = String.valueOf(areaSnapshot.child("dsignation").getValue());
                        emaino1 = String.valueOf(areaSnapshot.child("emaino1").getValue());
                        emaino2 = String.valueOf(areaSnapshot.child("emaino2").getValue());
                        String imgurl = String.valueOf(areaSnapshot.child("imgurl").getValue());
                        String phonenumber = String.valueOf(areaSnapshot.child("phonenumber").getValue());
                        String username = String.valueOf(areaSnapshot.child("username").getValue());
                        String whatsappno = String.valueOf(areaSnapshot.child("whatsappno").getValue());
                        Log.e("USER_PROFILE", " " + flag + " " + backuupID + " " + dsignation + " " + emaino1 + " "
                                + emaino2 + " " + imgurl + " " + phonenumber + " " + username + " " + whatsappno);
                        RegisterSetGet user = new RegisterSetGet(
                                backuupID,
                                username,
                                dsignation,
                                phonenumber,
                                whatsappno,
                                emaino1,
                                emaino2,
                                imgurl,
                                flag
                        );
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        if (user.getEmaino1().equals(imeiSIM1)) {
                            sendToken(imeiSIM1, imeiSIM2);
                        } else {
                            Toast.makeText(getApplicationContext(), "User Not Valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(Login.this,"User Phone Number Not Valid",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                            Toast.makeText(Login.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            Intent mIntent = new Intent(getApplicationContext(), MyJobIntentService.class);
                            mIntent.putExtra("maxCountValue", 1000);
                            MyJobIntentService.enqueueWork(getApplicationContext(), mIntent);
                            StartService();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
