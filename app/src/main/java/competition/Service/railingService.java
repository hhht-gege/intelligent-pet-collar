package competition.Service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import competition.MainActivity_ViewBinding;
import competition.R;
import competition.fragment.Me.MeFragment;
import competition.fragment.Setting.Javabean.CollarMsg;
import competition.fragment.Setting.Set_show;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class railingService extends Service {
    private static final int NOTIF_ID = 1;
    private static final String CHANNEL_ID = "com.competition.notification.channel";
    private static String json;

    private IntentFilter intentFilter;
    private LocalBroadcastManager manager;
    private railingReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("railingService", "onCreate: railingservice");
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("railingService", "????????????????????????");
    }

    public void InitBroad() {
        manager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("competition.Service.railing_Localbroadcast");
        receiver = new railingReceiver();
        manager.registerReceiver(receiver, intentFilter);
    }


    /**
     * ????????????????????????
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.N)
    public void setForegroundService() {
        //???????????????????????????
        String channelName = getString(R.string.app_name);
        //???????????????????????????
        int importance = NotificationManager.IMPORTANCE_HIGH;
        //??????????????????
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        //???????????????????????????????????????
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher) //??????????????????
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("????????????")//??????????????????
                .setContentText("???????????????????????????")//??????????????????
                .setOngoing(true);//????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        //??????????????????????????? NOTIFICATION_ID???????????????????????????ID
        startForeground(NOTIF_ID, builder.build());
    }


    public  class railingReceiver extends BroadcastReceiver {
        private String ip;
        private String petname;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("railingrecevier", "i need");
            json = intent.getStringExtra("json");
            ip=intent.getStringExtra("ip");
            petname=intent.getStringExtra("name");
            updateNotification(petname+"???????????????????????????",context);
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
