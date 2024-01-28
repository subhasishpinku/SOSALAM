package alam.sos.sosalam;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import alam.sos.sosalam.SetgetActivity.AlertSetGet;
import alam.sos.sosalam.SetgetActivity.ImageUploadInfo;
import alam.sos.sosalam.SetgetActivity.usaralamDataShowUpdate;

public class CreatealertActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    LinearLayout saveId;
    EditText EnterTextID;
    TextView dateId,timeId;
    Button cameraID,uploadID;
    private static final int Image_Capture_Code = 1;
    ImageView imageId;
    Uri FilePathUri;
    String url;
    ProgressDialog progressDialog;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    String Storage_Path = "Alert_Image/";
    public static final String Database_Path = "AlertImge_Upload";
    private int nextClassifiedID;
    private boolean isEdit;
    List<ImageUploadInfo> adsList;
    String imagename,imageurl;
    SharedPreferences sp;
    String ALERTMG = "1";
    String backupIdd = "1";
    String backupId,text,dateIdd,timeIdd,Imageulr;
    ArrayList<usaralamDataShowUpdate> List;
    String urlDowndload;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static final String PUSH_NOTIFICATION = "pushNotification";
    TextView txtDeviceToken;
    private String deviceToken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_createalert);
        saveId = (LinearLayout)findViewById(R.id.saveId);
        saveId.setOnClickListener(this);
        initToolBar();
        EnterTextID =(EditText)findViewById(R.id.EnterTextID);
        dateId = (TextView)findViewById(R.id.dateId);
        timeId = (TextView)findViewById(R.id.timeId);
        cameraID =(Button)findViewById(R.id.cameraID);
        cameraID.setOnClickListener(this);
        uploadID = (Button)findViewById(R.id.uploadID);
        uploadID.setOnClickListener(this);
        imageId = (ImageView)findViewById(R.id.imageId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        dateId.setText(date);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strtime =  mdformat.format(calendar.getTime());
        timeId.setText(strtime);
        progressDialog = new ProgressDialog(CreatealertActivity.this);
        requestStoragePermission();
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        sp = getSharedPreferences(Consts.ALERTMG, MODE_PRIVATE);
        SharedPreferences.Editor edit1 = sp.edit();
        edit1.putString("ALERTMG", "0");
        edit1.commit();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
//        databaseReference1.child("usaralamDataShow").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final List<String> name = new ArrayList<String>();
//                List = new ArrayList<usaralamDataShowUpdate>();
//                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
//                    backupId = String.valueOf(areaSnapshot.child("backupId").getValue());
//                    text = String.valueOf(areaSnapshot.child("text").getValue());
//                    dateIdd = String.valueOf(areaSnapshot.child("dateId").getValue());
//                    timeIdd = String.valueOf(areaSnapshot.child("timeId").getValue());
//                    Imageulr = String.valueOf(areaSnapshot.child("imageulr").getValue());
//                    Log.e("USERALAM",backupId+" "+text+" "+dateIdd+" "+timeIdd+" "+Imageulr);
//                    List.add(areaSnapshot.getValue(usaralamDataShowUpdate.class));
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


//        deviceToken = SharedPrefManager.getInstance(CreatealertActivity.this).getDeviceToken();
//        if(deviceToken != null){
//            displayDeviceRegId();
//        }
//
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                if (intent.getAction().equalsIgnoreCase(PUSH_NOTIFICATION)){
//                    displayDeviceRegId();
//                }
//
//            }
//        };


    }

    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Alert");
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
//    private void displayDeviceRegId() {
//
//        deviceToken = SharedPrefManager.getInstance(CreatealertActivity.this).getDeviceToken();
//        Log.e("FCM Device token",deviceToken);
//        Toast.makeText(getApplicationContext(), "Push notification: " + deviceToken, Toast.LENGTH_LONG).show();
//        txtDeviceToken.setText(deviceToken);
//    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveId:
                final String user = EnterTextID.getText().toString().trim();
                if (TextUtils.isEmpty(user)) {
                    EnterTextID.setError("Please enter Text");
                    EnterTextID.requestFocus();
                    return;
                }

                if (ALERTMG==sp.getString("ALERTMG","")) {
                    getClassifiedsFromDb(EnterTextID.getText().toString());
                    addAlamaData();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Upload Image", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cameraID:
                takePhotoFromCamera();
                break;
            case R.id.uploadID:
                sp = getSharedPreferences(Consts.ALERTMG, MODE_PRIVATE);
                SharedPreferences.Editor edit1 = sp.edit();
                edit1.putString("ALERTMG", "1");
                edit1.commit();
                UploadImageFileToFirebaseStorage();
                break;
            default:
        }
    }
    private void takePhotoFromCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Image_Capture_Code);
        } else {

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
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    takePhotoFromCamera();
                }
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
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
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatealertActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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
                        urlDowndload = String.valueOf(task.getResult());
                        Log.e("URL",""+downUri);
                        progressDialog.dismiss();
                        ImageUploadInfo imageUploadInfo = new ImageUploadInfo(EnterTextID.getText().toString(), downUri.toString());
                        String ImageUploadId = databaseReference.push().getKey();
                        databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        usaralamDataShowUp(backupIdd,EnterTextID.getText().toString(),dateId.getText().toString(),timeId.getText().toString(),urlDowndload);
                    }
                }
            });
        }
        else {

            Toast.makeText(CreatealertActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }

    public void addAlamaData() {
        AlertSetGet alertSetGet = createClassifiedAdObj();
        addClassifiedToDB(alertSetGet);

    }
    private void addClassifiedToDB(final AlertSetGet alertSetGet) {
        final DatabaseReference idDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("AlertdataID").child("id");
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
                    addClassified(alertSetGet, ""+nextClassifiedID);
                } else {

                }

            }
        });
    }
    private void getClassifiedsFromDb(final String text) {
        dbRef.child("AlertImge_Upload").orderByChild("imageName")
                .equalTo(text).addListenerForSingleValueEvent(new ValueEventListener() {
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
//                    addAlamaData(imageurl);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Toast.makeText(getApplicationContext(), "Error trying to get Date ads for " + userdb, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private AlertSetGet createClassifiedAdObj() {
        final AlertSetGet ad = new AlertSetGet();
        ad.setText(EnterTextID.getText().toString());
        ad.setDateId(dateId.getText().toString());
        ad.setTimeId(timeId.getText().toString());
        ad.setImageulr(urlDowndload);
        return ad;

    }
    private void restUi() {
      //  ((EditText)findViewById(R.id.EnterTextID)).setText("");
        String mess = EnterTextID.getText().toString();
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        Bundle bundle_edit  =   new Bundle();
        bundle_edit.putString("mess",mess);
        intent.putExtras(bundle_edit);
        startActivity(intent);
    }
    private void addClassified(AlertSetGet alertSetGet, String backupId) {
        alertSetGet.setBackupId(backupId);
        dbRef.child("Alertdata").child(backupId)
                .setValue(alertSetGet)
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


    public void usaralamDataShowUp(final String backupId,final String text,final String dateId,final String timeId,final String Imageulr) {
        usaralamDataShowUpdate usaralamDataShowUpdate = UpdatelassifiedAdObj(backupId,text,dateId,timeId,Imageulr);
        updateClassifiedToDB(usaralamDataShowUpdate,backupId);
    }
    private usaralamDataShowUpdate UpdatelassifiedAdObj(final String backupId, final String text, final String dateId, final String timeId, final String Imageulr) {
        final usaralamDataShowUpdate ad = new usaralamDataShowUpdate();
        ad.setBackupId(backupId);
        ad.setText(text);
        ad.setDateId(dateId);
        ad.setTimeId(timeId);
        ad.setImageulr(Imageulr);
        return ad;
    }
    private void updateClassifiedToDB(usaralamDataShowUpdate usaralamDataShowUpdate, String userId) {
        addClassifiedd(usaralamDataShowUpdate, userId);
        Log.e("usaralamDataShow",userId);
    }
    private void addClassifiedd(usaralamDataShowUpdate usaralamDataShowUpdate, String userId) {
        usaralamDataShowUpdate.setBackupId(userId);
        databaseReference1.child("usaralamDataShow").child(userId)
                .setValue(usaralamDataShowUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e("Inside onResume","onStart called when activity is becoming visible to the user");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("Inside onResume","onResume called when activity will start interacting with the user");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(PUSH_NOTIFICATION));
    }

    @Override
    public void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        Log.e("Inside onResume","onPause called when activity is not visible to the user");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e("Inside onResume","onStop called when activity is no longer visible to the user");
    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.e("Inside onResume","onRestart called after your activity is stopped, prior to start");

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("Inside onResume","onDestroy called before the activity is destroyed");
    }
}
