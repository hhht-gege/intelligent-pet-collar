package competition.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

import competition.R;
import competition.fragment.Me.MeFragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class tempService extends Service {
    private String json;
    private static final int NOTIF_ID=2;
    private static final String CHANNEL_ID = "com.competition.notification.channel.temp";

    private IntentFilter intentFilter;
    private LocalBroadcastManager manager;
    private tempReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        setForegroundService();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForegroundService();
        InitBroad();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void InitBroad() {
        manager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("competition.Service.temp_Localbroadcast");
        receiver = new tempReceiver();
        manager.registerReceiver(receiver, intentFilter);
    }

    /**
     *????????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.N)
    public void  setForegroundService()
    {
        //???????????????????????????
        String channelName = getString(R.string.app_name);
        //???????????????????????????
        int importance = NotificationManager.IMPORTANCE_HIGH;
        //??????????????????
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        //???????????????????????????????????????
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher) //??????????????????
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentTitle("???????????? ")//??????????????????
                .setContentText("????????????????????????")//??????????????????
                .setOngoing(true);//????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        //??????????????????????????? NOTIFICATION_ID???????????????????????????ID
        startForeground(NOTIF_ID,builder.build());
    }


    public class tempReceiver extends BroadcastReceiver {
        private String ip;
        private String petname;
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("temprecevier", "hello");
            json = intent.getStringExtra("json");
            ip=intent.getStringExtra("ip");
            petname=intent.getStringExtra("name");
            updateNotification(petname+"??????????????????",context);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private Notification getMyActivityNotification(String text,Context context) {
            //???????????????????????????
            String channelName = String.valueOf(R.string.app_name);
            //???????????????????????????
            int importance = NotificationManager.IMPORTANCE_HIGH;
            //??????????????????-
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{1000, 2000});
            return new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("????????????")
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .getNotification();
        }

        /**
         * This is the method that can be called to update the Notification
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void updateNotification(String text,Context context) {

            Notification notification = getMyActivityNotification(text,context);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIF_ID, notification);
        }
    }

}
