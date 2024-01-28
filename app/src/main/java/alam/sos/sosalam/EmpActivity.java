package alam.sos.sosalam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import alam.sos.sosalam.ImeiTelephone.TelephonyInfo;
import alam.sos.sosalam.SetgetActivity.ImageUploadInfo;
import alam.sos.sosalam.SetgetActivity.RegisterSetGet;

public class EmpActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AddRegister";
    Toolbar toolbar;
    LinearLayout saveId;
    EditText username,desigId,phId,whatsappId;
    EditText  emiId,emiiId;
    private static final int Image_Capture_Code = 1;
    Button cameraId;
    ImageView imageId;
    String url;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private int nextClassifiedID;
    private boolean isEdit;
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String Storage_Path = "All_Image_Uploads/";
    public static final String Database_Path = "ProfileImage_Upload";
    int Image_Request_Code = 7;
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
    String imagename,imageurl;
    List<ImageUploadInfo> adsList;
    Button upload;
    SharedPreferences sp;
    String PROFILEIMG = "1";
    String phoneNumber;
    Spinner spID;
    String DisgName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_emp);
        initToolBar();
        saveId =(LinearLayout) findViewById(R.id.saveId);
        username = (EditText)findViewById(R.id.username);
      //  desigId = (EditText)findViewById(R.id.desigId);
        phId = (EditText)findViewById(R.id.phId);
        whatsappId = (EditText)findViewById(R.id.whatsappId);
        emiId = (EditText) findViewById(R.id.emiId);
        emiiId = (EditText)findViewById(R.id.emiiId);
        saveId.setOnClickListener(this);
        cameraId = (Button)findViewById(R.id.cameraId);
        cameraId.setOnClickListener(this);
        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(this);
        imageId =(ImageView)findViewById(R.id.imageId);
        spID =(Spinner)findViewById(R.id.spID);
        requestStoragePermission();
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        progressDialog = new ProgressDialog(EmpActivity.this);
        askForPermission(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
        numbers = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = SubscriptionManager.from(EmpActivity.this);
        }
        showDeviceInfo();
        sp = getSharedPreferences(Consts.PROFILEIMG, MODE_PRIVATE);
        SharedPreferences.Editor edit1 = sp.edit();
        edit1.putString("PROFILEIMG", "0");
        edit1.commit();
        spID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                DisgName = spID.getSelectedItem().toString();
                  Toast.makeText(parent.getContext()," "+" "+DisgName, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        dbRef.child("Desig_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("desgId").getValue(String.class);
                    areas.add(areaName);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(EmpActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spID.setAdapter(areasAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Employee");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }



    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.saveId:
              final String user = username.getText().toString().trim();
          //    final String dsignation = desigId.getText().toString().trim();
              final String phone = phId.getText().toString().trim();
              final String whats = whatsappId.getText().toString().trim();
              final String emai1 = emiId.getText().toString().trim();
              final String emai2 = emiiId.getText().toString().trim();
              if (TextUtils.isEmpty(user)) {
                  username.setError("Please enter UserName");
                  username.requestFocus();
                  return;
              }
//              if (TextUtils.isEmpty(dsignation)) {
//                  desigId.setError("Please enter Designation");
//                  desigId.requestFocus();
//                  return;
//              }

              if (TextUtils.isEmpty(phone)) {
                  phId.setError("Please enter PhoneNumber");
                  phId.requestFocus();
                  return;
              }
              if(phone.isEmpty()||phId.getText().toString().length()<10 || phId.length()>11 ){
                  Toast.makeText(this, "Enter Phone Numner 10 Digit", Toast.LENGTH_LONG).show();
              }
              if (TextUtils.isEmpty(whats)) {
                  whatsappId.setError("Please enter WhatsappNumber");
                  whatsappId.requestFocus();
                  return;
              }
              if(whats.isEmpty()||whatsappId.getText().toString().length()<10 || whatsappId.length()>11 ){
                  Toast.makeText(this, "Enter WhatsApp  Numner 10 Digit", Toast.LENGTH_LONG).show();
              }
              if (TextUtils.isEmpty(emai1)) {
                  emiId.setError("Please enter Emai1");
                  emiId.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(emai2)) {
                  emiiId.setError("Please enter Emai2");
                  emiiId.requestFocus();
                  return;
              }
              getClassifiedsFromDb(imeiSIM1);
              if (PROFILEIMG==sp.getString("PROFILEIMG","")) {
                  addRegister();
              }
              else {
                  Toast.makeText(getApplicationContext(), "Please Upload Image", Toast.LENGTH_SHORT).show();
              }
              break;
          case R.id.cameraId:
              takePhotoFromCamera();
              break;
          case R.id.upload:
              sp = getSharedPreferences(Consts.PROFILEIMG, MODE_PRIVATE);
              SharedPreferences.Editor edit1 = sp.edit();
              edit1.putString("PROFILEIMG", "1");
              edit1.commit();
              UploadImageFileToFirebaseStorage();
              break;
              default:
      }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    takePhotoFromCamera();
                }
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imei = getImeiNumber();
                    getClientPhoneNumber();
                    androidid = getAndroidId();
                    AllEmai();

                } else {
                     Toast.makeText(EmpActivity.this, "You have Denied the Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imageId.setImageBitmap(bp);
                Uri tempUri = getImageUri(getApplicationContext(), bp);
                FilePathUri =  getImageUri(getApplicationContext(), bp);
                File finalFile = new File(getRealPathFromURII(tempUri));
                Log.e("IMGPATH"," "+finalFile);
                url = String.valueOf(finalFile);

                Log.e("path", url);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURII(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    private void takePhotoFromCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Image_Capture_Code);
        } else {

        }
    }

    public void addRegister() {
        RegisterSetGet registerSetGet = createClassifiedAdObj();
        addClassifiedToDB(registerSetGet);
        getClassifiedsFromDb(imeiSIM1);
    }
    private void addClassifiedToDB(final RegisterSetGet registerSetGet) {
        final DatabaseReference idDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("Register_UserID").child("id");
        idDatabaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(int.class) == null) {
                    idDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //set initial value
                            if(dataSnapshot != null && dataSnapshot.getValue() == null){
                                idDatabaseRef.setValue(0);
                                idDatabaseRef.setValue(1);


                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    return Transaction.abort();
                }
                nextClassifiedID = mutableData.getValue(int.class);
                Log.e("adId"," "+nextClassifiedID);
                mutableData.setValue(nextClassifiedID + 1);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean state,
                                   DataSnapshot dataSnapshot) {
                if (state) {
                    addClassified(registerSetGet, ""+nextClassifiedID);
                } else {

                }

            }
        });
    }

    private RegisterSetGet createClassifiedAdObj() {
        final RegisterSetGet ad = new RegisterSetGet();
        ad.setUsername(username.getText().toString());
        ad.setDsignation(DisgName);
        ad.setPhonenumber(phId.getText().toString());
        ad.setWhatsappno(whatsappId.getText().toString());
        ad.setEmaino1(emiId.getText().toString());
        ad.setEmaino2(emiiId.getText().toString());
        ad.setImgurl(imageurl);
        ad.setFlag("0");
        return ad;
    }
    private void getClassifiedsFromDb(final String imeiSIM1) {
        Log.e("Data","Data");
        dbRef.child("ProfileImage_Upload").orderByChild("imageName")
                .equalTo(imeiSIM1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("USER"," "+dataSnapshot.getValue());
                adsList = new ArrayList<ImageUploadInfo>();
                ImageUploadInfo imageUploadInfo = new ImageUploadInfo();
                for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                    adsList.add(adSnapshot.getValue(ImageUploadInfo.class));
                    imagename = String.valueOf(adSnapshot.child("imageName").getValue());
                    imageurl = String.valueOf(adSnapshot.child("imageURL").getValue());
                    Log.e("ImageUrl",imagename+" "+imageurl);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Toast.makeText(getApplicationContext(), "Error trying to get Date ads for " + userdb, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void restUi() {
        ((EditText)findViewById(R.id.username)).setText("");
       // ((EditText)findViewById(R.id.desigId)).setText("");
        ((EditText) findViewById(R.id.phId)).setText("");
        ((EditText) findViewById(R.id.whatsappId)).setText("");
        ((EditText) findViewById(R.id.emiId)).setText("");
        ((EditText) findViewById(R.id.emiiId)).setText("");
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
    private void addClassified(RegisterSetGet registerSetGet, String backupId) {
        registerSetGet.setBackuupID(backupId);
        dbRef.child("Register_User").child(backupId)
                .setValue(registerSetGet)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(isEdit){
                                addClassifieds();
                            }else{
                                restUi();

                            }
                            Toast.makeText(getApplicationContext(), "Save Data Successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Save Data Not Successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addClassifieds() {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), EmpActivity.class);
        startActivity(i);
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }
    public void UploadImageFileToFirebaseStorage() {
        if (FilePathUri != null) {
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();
            final StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String TempImageName = username.getText().toString().trim();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(EmpActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Error", exception.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Image is Uploading...");
                        }
                    });

            storageReference2nd.putFile(FilePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference2nd.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        Log.e("URL",""+downUri);
                        progressDialog.dismiss();
                        ImageUploadInfo imageUploadInfo = new ImageUploadInfo(imeiSIM1, downUri.toString());
                        String ImageUploadId = databaseReference.push().getKey();
                        databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                    }
                }
            });
        }
        else {

            Toast.makeText(EmpActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(EmpActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EmpActivity.this, permission)) {
                ActivityCompat.requestPermissions(EmpActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(EmpActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = getImeiNumber();
            Log.e("IMEII",imei);
            getClientPhoneNumber();
            androidid=getAndroidId();
          //     Toast.makeText(this,permission + " is already granted.", Toast.LENGTH_SHORT).show();
            AllEmai();
//          checkServiceStatus();
        }
    }
    private void AllEmai(){
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        imeiSIM1 = telephonyInfo.getImsiSIM1();
        imeiSIM2 = telephonyInfo.getImsiSIM2();
        emiId.setText(imeiSIM1);
        emiiId.setText(imeiSIM2);
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
        Toast.makeText(EmpActivity.this, "Requesting permission", Toast.LENGTH_SHORT).show();
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
        super.onDestroy();
        System.out.println("Inside onDestroy");
    }


}
