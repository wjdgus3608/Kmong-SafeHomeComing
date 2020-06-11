package com.jung.safedrive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DriveActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private static LocationManager lm=null;
    private static LatLng mMyLocation=null;
    private static SensorManager mSensor=null;
    private static Sensor mAcceler;
    private MapView mMapView;
    private GoogleMap mMap;
    private String phoneNumber;
    private Bundle mData;
    private Handler mHandler;
    private long interval=-1;
    private long shakeTime;
    private long shakeCount = 0;
    private boolean isStarted = false;
    private static final int SHAKE_TARGET_COUNT = 10;
    private static final int SHAKE_SKIP_TIME = 500;
    private static final float SHAKE_GRAVITY = 2.7F;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        Intent intent = getIntent();
        mData=intent.getExtras();
        mHandler=new Handler();
//37.3549, -121.9315
        mMapView=findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mSensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAcceler=mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        setPermissions();

        Button startBtn = findViewById(R.id.drive_start_btn);
        Button endBtn = findViewById(R.id.drive_end_btn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted=true;
                TextView stateView = findViewById(R.id.drive_state_view);
                Toast.makeText(getApplicationContext(),"추적을 시작합니다.",Toast.LENGTH_SHORT).show();
                stateView.setVisibility(View.VISIBLE);
                requestGPS();
                int index=mData.getInt("Alarminterval");
                switch (index){
                    case 0:
                        interval=60000;
                        break;
                    case 1:
                        interval=180000;
                        break;
                    case 2:
                        interval=300000;
                        break;
                }
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        sendSMS(0);
                        mHandler.postDelayed(this,interval);
                    }
                };
                mHandler.postDelayed(runnable,interval);
            }
        });
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted=false;
                TextView stateView = findViewById(R.id.drive_state_view);
                Toast.makeText(getApplicationContext(),"추적을 종료합니다.",Toast.LENGTH_SHORT).show();
                stateView.setVisibility(View.INVISIBLE);
                mHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    private void setPermissions(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_DENIED) {
            Log.d("permission", "permission denied to SEND_SMS - requesting it");
            String[] permissions = {Manifest.permission.SEND_SMS};

            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
    }

    private void sendSMS(int mode){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_DENIED) {
            Log.d("permission", "permission denied to SEND_SMS - requesting it");
            String[] permissions = {Manifest.permission.SEND_SMS};

            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            sendSMS(mode);
        }
        else{
            try {
                Toast.makeText(this,"메세지가 전송됩니다.",Toast.LENGTH_SHORT).show();
                phoneNumber=mData.getString("phoneNumber");
                String message=(mode==1?"긴급상황\n":"");
                message+="차량번호 : "+mData.getString("carNumber","미입력")+"\n"+"현재 위치 :\n 위도 "+mMyLocation.latitude+"\n"+" 경도 "+mMyLocation.longitude;
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber,null,message,null,null);
                Log.d("log","sended!");
            }
            catch (Exception e){
                Log.d("log",e.toString());
            }

        }
    }

    private void requestGPS(){
        final LocationListener gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(!isStarted) return;
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                mMyLocation=new LatLng(latitude,longitude);
                MarkerOptions marker=new MarkerOptions().position(mMyLocation).title("내 위치");
                mMap.clear();
                mMap.addMarker(marker);
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mMyLocation));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
            requestGPS();
        }
        else{
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 1, gpsListener);
        }
    }

    private void calculateIsShake(SensorEvent event){
        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        float gravityX = axisX / SensorManager.GRAVITY_EARTH;
        float gravityY = axisY / SensorManager.GRAVITY_EARTH;
        float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

        float f = (float) (Math.pow(gravityX,2) + Math.pow(gravityY,2) + Math.pow(gravityZ,2));
        double D = Math.sqrt(f);
        float gForce = (float) D;
        if(gForce > SHAKE_GRAVITY){
            long currentTime = System.currentTimeMillis();
            if(shakeTime + SHAKE_SKIP_TIME > currentTime){
                return;
            }
            shakeTime = currentTime;
            shakeCount++;
            if(shakeCount == 0)
                initCountPassed(30000);
            else if(shakeCount == SHAKE_TARGET_COUNT){
                sendSMS(1);
                shakeCount=0;
            }
            Toast.makeText(this,"Shaked! "+shakeCount,Toast.LENGTH_SHORT).show();

        }
    }

    private void initCountPassed(long sec){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shakeCount=0;
            }
        },sec);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if(isStarted && mData.getBoolean("Shakealarmcheck"))
            calculateIsShake(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mMapView!=null){
            mMapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMapView!=null){
            mMapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMapView!=null){
            mMapView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMapView!=null){
            mMapView.onResume();
        }
        mSensor.registerListener(this,mAcceler,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMapView!=null){
            mMapView.onPause();
        }
        mSensor.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacksAndMessages(null);
    }
}
