package competition.fragment.Setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;

import competition.R;
import competition.Service.MyService;
import competition.Utils.API;
import competition.Utils.Constanst;
import competition.Utils.RetrofitManager;
import competition.fragment.Setting.Javabean.CollarMsg;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Set_show extends AppCompatActivity {
    private final String TAG="Set_show";
    private MapView mapView;
    private AMap map;
    private String ip;
    private String petname_str;

    private static MaterialTextView topic;
    private static MaterialTextView hum;
    private static MaterialTextView petname;
    private static MaterialTextView heart;
    private static MaterialTextView temp;
    private static MaterialTextView step;
    private static MaterialTextView latitude;
    private static MaterialTextView longitude;

    private IntentFilter intentFilter;
    private LocalBroadcastManager manager;
    private NetworkChangeReceiver receiver;
    private  Intent intentService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_show);
        mapView=(MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        map=mapView.getMap();
        InitView();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                InitService();
            }
        });
        t.start();
    }
    public void InitView(){
        topic=(MaterialTextView) findViewById(R.id.set_show_topic);
        hum=(MaterialTextView) findViewById(R.id.set_show_humidity);
        heart=(MaterialTextView) findViewById(R.id.set_show_heart);
        petname=(MaterialTextView) findViewById(R.id.set_show_petname);
        step=(MaterialTextView) findViewById(R.id.set_show_step);
        temp=(MaterialTextView) findViewById(R.id.set_show_temp);
        latitude=(MaterialTextView) findViewById(R.id.set_show_latitude);
        longitude=(MaterialTextView) findViewById(R.id.set_show_longitude);
        getPetname();

    }

    public void InitService(){
        InitReceiver();
        intentService=new Intent(this, MyService.class);
        intentService.putExtra("ip",ip);
        startService(intentService);
    }

    public void InitReceiver(){
        ip=getIntent().getStringExtra("ip");
        topic.setText("??????ip: "+ip);
        manager=LocalBroadcastManager.getInstance(this);
        intentFilter=new IntentFilter();
        intentFilter.addAction("competition.Service.Set_show_Localbroadcast"+ip);
        receiver=new NetworkChangeReceiver();
        manager.registerReceiver(receiver,intentFilter);
    }


    public void InitData(CollarMsg collarMsg) throws InterruptedException {
        hum.setText("??????: "+collarMsg.getHumidity());
        heart.setText("??????: "+String.format("%.5f",collarMsg.getHeartrate()));
        temp.setText("??????: "+collarMsg.getTemp());
        latitude.setText("??????: "+String.format("%.5f",collarMsg.getLatitude()));
        longitude.setText("??????: "+String.format("%.5f",collarMsg.getLongitude()));
        Random random = new Random();
        int steps=100+random.nextInt(901);
        step.setText("??????: "+steps);
        AddMarker(collarMsg.getLatitude(),collarMsg.getLongitude());
    }

    public void getPetname(){
        petname_str=getIntent().getStringExtra("petName");
        if(petname_str==null)
            petname_str="???????????????";
        petname.setText("????????????:"+petname_str);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //???activity??????onResume?????????mMapView.onResume ()???????????????????????????
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView.onPause ()????????????????????????
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //???activity??????onSaveInstanceState?????????mMapView.onSaveInstanceState (outState)??????????????????????????????
        mapView.onSaveInstanceState(outState);
    }
    //????????????
    public void AddMarker(double v,double v1)  {
        //v?????????v1??????
        LatLng latLng=new LatLng(v,v1);
        Marker marker=map.addMarker(new MarkerOptions().position(latLng));
        //???????????????????????????????????????????????????????????????????????????????????????????????????0??~45??????????????????????????0??????????????? 0~360?? (????????????0)
        CameraUpdate cameraUpdate= CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,14,0,0));
        map.moveCamera(cameraUpdate);
    }

  @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"?????????");
        stopService(intentService);
    }

   public  class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson=new Gson();
            String json=intent.getStringExtra("json");
            Log.d("network",json);
            CollarMsg body=gson.fromJson(json,CollarMsg.class);
            try {
                InitData(body);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("????????????????????????");
            }

        }
    }
}
