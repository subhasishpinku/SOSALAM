package alam.sos.sosalam.BootActivity;//package com.example.sos.BootActivity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.example.sos.HomeActivity;
//
//public class StartActivityBootReciver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
//        Intent i = new Intent(context, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//        Log.e("BOOT","BOOT");
//    }
//    }
//}
