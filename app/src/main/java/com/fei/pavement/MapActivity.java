package com.fei.pavement;

import android.Manifest;
import android.app.Activity;
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
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends Activity implements SensorEventListener {
    ArrayList<See> allSee = new ArrayList();
    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;
    private SensorManager mSensorManager;
    private TextView textViewJing;
    private TextView textViewWei;
    //    StringBuilder sb;
//    StringBuilder sb2;
    int iii = 0;
    StringBuilder sb3;
    double[] panduan = new double[20000];
    int size;
    double[] outData = new double[20000];
    int size2 = 0;
    double[] v_data = new double[20000];
    int size3 = 0;

    double[] jingdu = new double[20000];
    int size5 = 0;
    double[] weidu = new double[20000];
    int size6 = 0;

    gaussion gs = new gaussion();
    Butterworth butterworth = new Butterworth();
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient;

    private void showUserLocation(BDLocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        Log.e("feifei",location.getLatitude()+" "+location.getLongitude());
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);// 设置地图放大比例
        mBaiduMap.setMapStatus(msu);
        msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
//            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(200.0f);// 设置地图放大比例
//            mBaiduMap.setMapStatus(msu);
//            msu = MapStatusUpdateFactory.newLatLng(latLng);
//            mBaiduMap.animateMapStatus(msu);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            writeLocation(location);
            Log.e("feifei13","经度="+location.getLongitude()+" 纬度="+location.getLatitude());
            mBaiduMap.setMyLocationData(locData);
            showUserLocation(location);
        }
    }
    public void writeLocation(BDLocation location){
        if (location != null) {
                //不为空,显示地理位置经纬度
                if (size >= 0) {
                    for (int i = 0; i < 200; i++) {
                        double speed = location.getSpeed();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        jingdu[size5] = longitude;
                        weidu[size6] = latitude;
                        v_data[size3] = speed;
                        textViewWei.setText(weidu[size6]+"");
                        textViewJing.setText(jingdu[size5]+"");
                        Log.e("feifei43", "size3=" + size3 + "   longitude" + longitude+"latitude"+location.getLatitude());
                        size3++;
                        size5++;
                        size6++;
//                        Log.e("feifei45",size+"  "+size3);
                        change();
                    }
                }


//                sb2=new StringBuilder();sb2.append("  "+location.getLongitude()+"  "+location.getLatitude()+"\n"+location.getSpeedAccuracyMetersPerSecond());
                //Toast.makeText(MainActivity.this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
        }
    }

    public void me_add(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.zhuyi);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option2 = new MarkerOptions()
                .position(point)
//                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //获取地图控件引用
        textViewJing=findViewById(R.id.jingdu);
        textViewWei=findViewById(R.id.weidu);
        Button textSee = findViewById(R.id.textSee);
        textSee.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, SeeAllKeng.class);
                        startActivity(intent);
                    }
                }
        );

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
//普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //定位初始化


        mLocationClient = new LocationClient(this);
//        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
//通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
//        option.setIgnoreKillProcess(false);
//设置locationClientOption
        mLocationClient.setLocOption(option);

//注册LocationListener监听器
//        showUserLocation();
        MyLocationListener myLocationListener = new MyLocationListener();

        mLocationClient.registerLocationListener(myLocationListener);
//开启地图定位图层
        mLocationClient.start();
//        28.06973 113.016689
//        allSee.add(new See(28.07031, 113.017826));
//        allSee.add(new See( 28.067292, 113.005076));
        allSee.add(new See(28.07684, 113.021069));
        allSee.add(new See(28.077677, 113.020791));
        allSee.add(new See(28.074226, 113.022039));
        allSee.add(new See(28.070027, 113.023647));
        allSee.add(new See(28.068082, 113.018235));
        allSee.add(new See(28.066735, 113.014013));
        allSee.add(new See(28.065301, 113.015226));
//        113.017826,28.07031
//        113.016792 纬度=28.069817
//        113.004604 28.067235
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://223.4.183.204:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ThingsService appService = retrofit.create(ThingsService.class);
//        appService.searchPage("1","200").enqueue(Object);
        appService.searchPage("1", "200").enqueue(new Callback<SearchPage>() {
            @Override
            public void onResponse(Call<SearchPage> call, Response<SearchPage> response) {
                SearchPage things1 = response.body();
                if (things1 != null) {

                    Log.e("feifei", allSee.size() + "");
                    try {
                        for (int i = 0; i < things1.getList().size(); i++) {
                            allSee.add(new See(Double.parseDouble(things1.getList().get(i).getLatitude()), Double.parseDouble(things1.getList().get(i).getLongitude())));
                        }
                        Log.e("feifei", allSee.size() + "");
                        for (int i = 0; i < allSee.size(); i++) {
                            LatLng point = new LatLng(allSee.get(i).getJingdu(), allSee.get(i).getWeidu());
                            me_add(point);
                        }
                    } catch (Exception e) {
                        Log.e("feifei", e.getMessage());
                    }
                } else {
                    Log.e("feifei2", "shibai");
                }
            }

            @Override
            public void onFailure(Call<SearchPage> call, Throwable t) {
                Log.e("feifei", t.getMessage());
            }
        });

        // 获取传感器管理对象
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        onReg();
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissions, 202);
        }
        getLocation();

        thread.start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //处理消息
            if (msg.what == 0) {
            }
            return false;
        }
    });

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            boolean flag = true;
            while (flag) {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);

                try {
                    thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public static void appendMethodB(String fileName, String content) {
        try {
//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onReg() {
        // 为加速度传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消监听
        mSensorManager.unregisterListener(this);
    }

    // 当传感器的值改变的时候回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        // 获取传感器类型
        int type = event.sensor.getType();
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                try {
                    panduan[size] = values[2];
//                    Log.e("feifei",panduan[size-1]+"");
                    size++;
                    if(size5!=0)
                    Log.e("feifei4", size + "  " + size3+" jingdu[]"+jingdu[size5-1]+" weidu[]"+weidu[size6-1]);
//                    12100
                    if (size >= 2000 && size3 >= 2000) {
//                        for(int i=0;i<1000;i++){
//                            Log.e("feifei9"," "+panduan[i]+"   v_data[size3]"+v_data[i]);
//                        }
                        IirFilterCoefficients iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 5, 10.0 / 50.0, 13.0 / 50.0);
                        outData = butterworth.IIRFilter(panduan, iirFilterCoefficients.a, iirFilterCoefficients.b);
                        final List<Integer> list;
                        list = gs.show(outData, v_data);
                        Log.e("feifei10",list.size()+"");
//                        try{
//                            for (int i = 0; i < list.size(); i++) {
////                            Log.e("feifei3", " " + list.get(i) + "");
//                                int t=list.get(i);
//                                Log.e("feifei12", "t="+t+" "+size + "  " + size3+" jingdu[]"+jingdu[t]+" weidu[]"+weidu[t]);
//                            }
//                        }catch (Exception e){
//                            Log.e("feifei12", e.getMessage());
//                        }

//                        for (int i = 0; i < list.size(); i++) {
                            if (list.size() > 10) {
//                                final int finnal = list.get();
//                                Log.e("feifei11","finnal="+finnal);
                                try {
                                    final int finnal=list.get(list.size()/2);
                                    final double me_jingdu=jingdu[finnal];
                                    final double me_weidu=weidu[finnal];
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {

                                                Log.e("feifei5", "finnal="+finnal+" "+ me_jingdu + " " + me_weidu);
                                                OkHttpClient client = new OkHttpClient();
                                                FormBody requestBody = new FormBody.Builder()
                                                        .add("latitude", me_weidu + "")
                                                        .add("longitude", me_jingdu + "")
                                                        .build();
                                                Request request = new Request.Builder()
                                                        .url("http://223.4.183.204:8080/Myproject/saveLatitudeLongitude")
                                                        .post(requestBody)
                                                        .build();
                                                okhttp3.Response response = client.newCall(request).execute();
                                                String responseData = response.body().string();
                                                if (responseData != null) {
                                                    Log.e("feifei", responseData);

                                                    if (Integer.parseInt(responseData) == 200) {
                                                        Log.e("feifei2", finnal + "");
                                                        LatLng point = new LatLng( me_weidu,me_jingdu);
                                                        me_add(point);
//构建Marker图标
//                                                        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                                                .fromResource(R.drawable.zhuyi);
////构建MarkerOption，用于在地图上添加Marker
//                                                        OverlayOptions option3 = new MarkerOptions()
//                                                                .position(point)
//                                                                .icon(bitmap);
////在地图上添加Marker，并显示
//                                                        mBaiduMap.addOverlay(option3);
                                                    }
                                                }

                                            } catch (IOException e) {
                                                Log.e("feifei6", e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }catch (Exception e){

                                }

                            }
                        size = 0;
                        panduan = new double[20000];
                        outData = new double[20000];
                        size2 = 0;
                        v_data = new double[20000];
                        size3 = 0;

                        jingdu = new double[20000];
                        size5 = 0;
                        weidu = new double[20000];
                        size6 = 0;
//                        }


                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    Log.e("feifei", e.getMessage());
                }

//                Date date = new Date(System.currentTimeMillis());
//
//                sb.append(simpleDateFormat.format(date)+" "+values[0]+"  ");
//                sb.append(values[1]+"  ");
//                sb.append(values[2]);
                break;

        }
    }

    // 当传感器精度发生改变时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        locationProvider = LocationManager.GPS_PROVIDER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                //监视地理位置变化
                locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (size >= 10)
                    for (int i = 0; i < 400; i++) {
                        if (location != null) {
                            //输入经纬度
                            v_data[size3] = location.getSpeed();
                            size3++;
                            jingdu[size5] = location.getLongitude();
                            weidu[size6] = location.getLatitude();
                            size5++;
                            size6++;
                            change();
                            Log.e("feifei42", size + "  " + size3);
                        }
                    }

            }
        } else {
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            for (int i = 0; i < 400; i++) {
                if (location != null) {
                    //不为空,显示地理位置经纬度
                    jingdu[size5] = location.getLongitude();
                    weidu[size6] = location.getLatitude();
                    v_data[size3] = location.getSpeed();
                    size3++;
                    size5++;
                    size6++;
                    Log.e("feifei43", size + "  " + size3);
                    change();
//                sb2=new StringBuilder();sb2.append("  "+location.getLongitude()+"  "+location.getLatitude());
                    // Toast.makeText(this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
//                //不为空,显示地理位置经纬度
//                if (size >= 0) {
//                    for (int i = 0; i < 500; i++) {
//                        double speed = location.getSpeed();
//                        double longitude = location.getLongitude();
//                        double latitude = location.getLatitude();
//                        jingdu[size5] = longitude;
//                        weidu[size6] = latitude;
//                        v_data[size3] = speed;
//                        textViewWei.setText(weidu[size6]+"");
//                        textViewJing.setText(jingdu[size5]+"");
//                        Log.e("feifei43", "size3=" + size3 + "   longitude" + longitude+"latitude"+location.getLatitude());
//                        size3++;
//                        size5++;
//                        size6++;
////                        Log.e("feifei45",size+"  "+size3);
//                        change();
//                    }
//                }
//
//
////                sb2=new StringBuilder();sb2.append("  "+location.getLongitude()+"  "+location.getLatitude()+"\n"+location.getSpeedAccuracyMetersPerSecond());
//                //Toast.makeText(MainActivity.this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请权限", Toast.LENGTH_LONG).show();
                    try {
                        List<String> providers = locationManager.getProviders(true);
//                        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//                            //如果是Network
//                            locationProvider = LocationManager.NETWORK_PROVIDER;
//                        }else if (providers.contains(LocationManager.GPS_PROVIDER)) {
//                            //如果是GPS
                        locationProvider = LocationManager.GPS_PROVIDER;
//                        }
                        //监视地理位置变化
                        locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        for (int i = 0; i < 400; i++) {
                            if (location != null) {
                                //不为空,显示地理位置经纬度
                                jingdu[size5] = location.getLongitude();
                                weidu[size6] = location.getLatitude();
                                v_data[size3] = location.getSpeed();
                                size3++;
                                size5++;
                                size6++;
                                Log.e("feifei4", size + "  " + size3);
                                change();
//                            sb2=new StringBuilder();sb2.append("  "+location.getLongitude()+"  "+location.getLatitude());
                                //Toast.makeText(MainActivity.this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "缺少权限", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
//        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
//        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        thread.interrupt();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mMapView.onDestroy();
    }

    void change() {
        if (size >= 18000 || size3 >= 18000) {
            size = 0;
            panduan = new double[20000];
            outData = new double[20000];
            size2 = 0;
            v_data = new double[20000];
            size3 = 0;

            jingdu = new double[20000];
            size5 = 0;
            weidu = new double[20000];
            size6 = 0;
        }
    }
}
