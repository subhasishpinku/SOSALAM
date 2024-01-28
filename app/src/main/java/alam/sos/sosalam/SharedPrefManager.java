package alam.sos.sosalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import alam.sos.sosalam.SetgetActivity.RegisterSetGet;

public class SharedPrefManager {
    //the constants
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_FLAG = "flag";
    private static final String KEY_BACKUPID = "backuupID";
    private static final String KEY_DISGNATION = "dsignation";
    private static final String KEY_EMAI1 = "emaino1";
    private static final String KEY_EMAI2 = "emaino2";
    private static final String KEY_IMAGE = "imgurl";
    private static final String KEY_PHONENUMBER = "phonenumber";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_WHATSAPP = "whatsappno";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(RegisterSetGet user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BACKUPID, user.getBackuupID());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_DISGNATION, user.getDsignation());
        editor.putString(KEY_PHONENUMBER, user.getPhonenumber());
        editor.putString(KEY_WHATSAPP, user.getWhatsappno());
        editor.putString(KEY_EMAI1, user.getEmaino1());
        editor.putString(KEY_EMAI2, user.getEmaino2());
        editor.putString(KEY_IMAGE, user.getImgurl());
        editor.putString(KEY_FLAG, user.getFlag());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method will give the logged in user
    public RegisterSetGet getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new RegisterSetGet(
                sharedPreferences.getString(KEY_BACKUPID, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_DISGNATION, null),
                sharedPreferences.getString(KEY_PHONENUMBER, null),
                sharedPreferences.getString(KEY_WHATSAPP, null),
                sharedPreferences.getString(KEY_EMAI1, null),
                sharedPreferences.getString(KEY_EMAI2, null),
                sharedPreferences.getString(KEY_IMAGE, null),
                sharedPreferences.getString(KEY_FLAG, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, Login.class));
    }
}
