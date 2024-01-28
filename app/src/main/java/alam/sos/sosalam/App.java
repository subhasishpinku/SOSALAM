package alam.sos.sosalam;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CH1 = "CHANEL1";
    public static final String CH2 = "CHANEL2";
    public static final String CH3 = "CHANEL3";
    @Override
    public void onCreate() {
        super.onCreate();
        create();
    }
    private void create(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.P)
        {
            NotificationChannel channel =new NotificationChannel(
                    CH1, "chanel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("This is channel 1");

            NotificationChannel channe2 =new NotificationChannel(
                    CH2, "chanel 2",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channe2.setDescription("This is channel 1");
            NotificationChannel channe3 =new NotificationChannel(
                    CH3, "chanel 3",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channe3.setDescription("This is channel 3");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channe2);
            manager.createNotificationChannel(channe3);

        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            NotificationChannel channel = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channel = new NotificationChannel(
//                        CH1, "chanel 1",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channel.setDescription("This is channel 1");
//            }
//
//            NotificationChannel channe2 = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channe2 = new NotificationChannel(
//                        CH2, "chanel 2",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channe2.setDescription("This is channel 1");
//            }
//            NotificationChannel channe3 = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channe3 = new NotificationChannel(
//                        CH3, "chanel 3",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                channe3.setDescription("This is channel 3");
//            }
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                manager.createNotificationChannel(channel);
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                manager.createNotificationChannel(channe2);
//            }
//        }

    }
}
