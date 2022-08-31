package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;
import android.os.Message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Home extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener , SensorEventListener{

    private final  String TAG = getClass().getSimpleName();

    String API_KEY = "l7xxf6f2a8d1177e4fcda307e964108a8fb0";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.INTERNET, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS};

    // T Map Tracking Mode
    private boolean m_bTrackingMode = true;
    // T Map View
    private TMapView tmapview = null;

    // POI 명칭
    private ArrayList<String> arrBuilding = new ArrayList<>();
    // T Map Data
    private TMapData tmapdata = null;

    // T Map GPS
    private TMapGpsManager tmapgps = null;

    private boolean gpsmode = true;

    boolean isRun = true;

    private String userAddress;
    private String parentsPhone;
    private Double lat = null;
    private Double lon = null;
    private float distance = 0.0F;


    private ImageButton bt_find; //주소로 경로 버튼
    static boolean find_mode = true;
    private ImageButton bt_fac;  //주변 안전지킴이 찾기 버튼
    boolean fac_mode = false;
    private ImageButton bt_home; //홈
    boolean home_mode = true    ;
    private ImageButton bt_gps;
    boolean gps_mode = false;
    private ImageButton bt_Chatbot; // 챗봇버튼
    private ImageButton bt_user;

    private AlertDialog.Builder builder;

    private double end_lat;
    private double end_lon;

    private TMapPoint startpoint;

    // 경로이탈 카운트
    static int count = 0;

    private String address;

    private MyThread myThread  = new <MyThread> MyThread();

    private AtomicInteger flag_out = new AtomicInteger(1);
    private AtomicInteger flag_naksang = new AtomicInteger(1);
    private AtomicInteger flag_timer = new AtomicInteger(1);

    // 낙상 감지
    SensorManager objSMG;                   // Object SensorManager
    Sensor sensor_Gyroscope;
    private static final float SHAKE_THRESHOLD = 10.0f;
    private long lastTime;
    private double abs;

    // 시간
    public int i_pathtime;

    private TMapPoint mygpspoint;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        Intent intent = getIntent();
        userAddress = intent.getStringExtra("userAddress");
        parentsPhone = intent.getStringExtra("parentsPhone");
        //경로이탈 푸쉬알림
        builder = new AlertDialog.Builder(Home.this);

        setTMapAuth(); //Tmap 각종 객체 선언
        setGPS(); // GPS 설정
        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        //버튼 선언
        bt_home = (ImageButton) findViewById(R.id.bt_home);
        bt_fac = (ImageButton) findViewById(R.id.bt_findfac);
        bt_find = (ImageButton) findViewById(R.id.bt_find);
        bt_gps = (ImageButton) findViewById(R.id.bt_gps);
        bt_Chatbot = (ImageButton) findViewById(R.id.chatbot);
        bt_user = (ImageButton) findViewById(R.id.bt_user);

        bt_gps.bringToFront();
        bt_Chatbot.bringToFront();

        //버튼 리스너 등록
        bt_fac.setOnClickListener(this);
        bt_find.setOnClickListener(this);
        bt_home.setOnClickListener(this);
        bt_gps.setOnClickListener(this);
        bt_Chatbot.setOnClickListener(this);
        bt_user.setOnClickListener(this);

        // 낙상 감지
        // Object for access sensor device
        objSMG = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensor_Gyroscope = objSMG.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }

    private void setTMapAuth() {
        tmapdata = new TMapData(); //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
        RelativeLayout RelativeLayout = (RelativeLayout) findViewById(R.id.mapview);
        tmapview = new TMapView(this);

        RelativeLayout.addView(tmapview);

        tmapview.setSKTMapApiKey(API_KEY);
        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);
        /* 줌레벨 */
        tmapview.setZoomLevel(15);
        /* 지도 타입 */
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        /* 언어 설정 */
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
    }

    private void setGPS() {
        tmapgps = new TMapGpsManager(Home.this); //단말의 위치탐색을 위한 클래스
        tmapgps.setMinTime(100); //위치변경 인식 최소시간설정
        tmapgps.setMinDistance(1); //위치변경 인식 최소거리설정
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //네트워크 기반의 위치탐색
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //위성기반의 위치탐색
        tmapgps.OpenGps();
    }

    ;

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(Home.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Home.this,  REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Home.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음

            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있다
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(Home.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(Home.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void findPath() {
        tmapview.removeAllMarkerItem();
        System.out.println(userAddress);

        Geocoder geocoder = new Geocoder(this);
        List<Address> userlist = null;
        try{
            userlist = geocoder.getFromLocationName(userAddress, 1);
        } catch (IOException e){
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        Address end_address = userlist.get(0);
        end_lat = end_address.getLatitude();
        end_lon = end_address.getLongitude();

        bt_home.setImageResource(R.drawable.home_off);
        bt_find.setImageResource(R.drawable.find_on);
        bt_fac.setImageResource(R.drawable.poi);

        startpoint = tmapgps.getLocation(); // 입력으로 수정해야
        TMapPoint endpoint = new TMapPoint(end_lat, end_lon);
        // 보행자 경로 탐색
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                polyLine.setLineWidth(30);
                tmapview.addTMapPath(polyLine);
            }
        });
        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                //System.out.print(document);
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("tmap:totalTime");
                for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    String s_pathtime = nodeListPlacemarkItem.item(i).getTextContent();
                    i_pathtime = Integer.parseInt(s_pathtime);
                    Log.d("test", s_pathtime);
//                    System.out.println(i_pathtime / 60);
                }
            }
        });
    }

    public void anomalyDetection(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("경로 이탈 감지");
        builder.setMessage("경로 이탈하셨습니다. 서비스 종료하겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isRun = false;
                flag_out.set(1);
                Toast.makeText(getApplicationContext(),"Clicked Yes",Toast.LENGTH_LONG);
                setBt_home();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("", "onClick: Yes");
                isRun = true;
                flag_out.set(1);
                Toast.makeText(getApplicationContext(),"Clicked No", Toast.LENGTH_SHORT).show();
                findPath();
            }
        });
        builder.show();
    }
    public void getDistance(double lat, double lon) {
        //TMapPoint startpoint = new TMapPoint(37.570841, 126.985302);

        TMapPoint endpoint = new TMapPoint(end_lat, end_lon);
        final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        Element root = document.getDocumentElement();
                        NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                        for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                            NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                            for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                                    String[] test = nodeListPlacemarkItem.item(j).getTextContent().trim().split(",");
                                    double path_lon = Double.parseDouble(test[0]);
                                    double path_lat = Double.parseDouble(test[1]);
                                    TMapPoint path_list = new TMapPoint(path_lat, path_lon);
                                    arrTMapPoint.add(path_list);
                                }
                            }
                        }
                        for (int i = 0; i < arrTMapPoint.size(); i++) {
                            double path_lon = arrTMapPoint.get(i).getLongitude();
                            double path_lat = arrTMapPoint.get(i).getLatitude();

                            Location mgps = new Location("My_gps");
                            Location safe = new Location("Safe");

                            mgps.setLatitude(lat);
                            mgps.setLongitude(lon);
                            safe.setLatitude(path_lat);
                            safe.setLongitude(path_lon);

                            distance = mgps.distanceTo(safe);
                            //System.out.println(distance);
                            String TAG = "";
                            //.out.println("밖에서" + count);
                            //System.out.println(count);
                            Log.d(TAG, Double.toString(distance));

                            if (distance < 30.0) {
                                //System.out.println("안에서" + count);
                                count++;
                            }

                            if (count ==0){
                                flag_out.set(0);
                            }


                        }
                    }
                }
        );
    }
    private void searchPOI() {
        tmapview.removeTMapPath();

        bt_home.setImageResource(R.drawable.home_off);
        bt_find.setImageResource(R.drawable.find);
        bt_fac.setImageResource(R.drawable.poi_on);


        final TMapData tMapData = new TMapData();
        final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
        final ArrayList<String> arrTitle = new ArrayList<>();
        final ArrayList<String> arrAddress = new ArrayList<>();
        TMapPoint point = tmapview.getCenterPoint();

        tMapData.findAroundNamePOI(point, "편의점;경찰서", 1, 30, new TMapData.FindAroundNamePOIListenerCallback() {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    arrTMapPoint.add(item.getPOIPoint());
                    arrTitle.add(item.getPOIName());
                    arrAddress.add(item.upperAddrName + " " +
                            item.middleAddrName + " " + item.lowerAddrName);
                }
                setMultiMarkers(arrTMapPoint, arrTitle, arrAddress);
            }
        });
    }

    private void setMultiMarkers(ArrayList<TMapPoint> arrTPoint, ArrayList<String> arrTitle,
                                 ArrayList<String> arrAddress) {
        for (int i = 0; i < arrTPoint.size(); i++) {
            Bitmap bitmapIcon = createMarkerIcon(R.drawable.ping);

            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
            tMapMarkerItem.setIcon(bitmapIcon);

            tMapMarkerItem.setTMapPoint(arrTPoint.get(i));

            tmapview.addMarkerItem("markerItem" + i, tMapMarkerItem);

            setBalloonView(tMapMarkerItem, arrTitle.get(i), arrAddress.get(i));
        }
    }
    private void setBalloonView(TMapMarkerItem marker, String title, String address) {
        marker.setCanShowCallout(true);

        if (marker.getCanShowCallout()) {
            marker.setCalloutTitle(title);
            marker.setCalloutSubTitle(address);
        }
    }
    private Bitmap createMarkerIcon(int image) {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                image);
        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);

        return bitmap;
    }
    private void gpsview() {
        if (gpsmode == true) {
            bt_gps.setImageResource(R.drawable.gps_1);
            tmapview.setTrackingMode(true);
            tmapview.setSightVisible(true);
            tmapview.setCompassMode(false);
            gpsmode = false;
        } else {
            bt_gps.setImageResource(R.drawable.gps_0);
            tmapview.setTrackingMode(true);
            tmapview.setSightVisible(true);
            /* 현재 보는 방향 */
            tmapview.setCompassMode(true);
            gpsmode = true;
        }

    }
    public void setBt_home(){

        bt_home.setImageResource(R.drawable.home_on);
        bt_find.setImageResource(R.drawable.find);
        bt_fac.setImageResource(R.drawable.poi);

        tmapview.removeTMapPath();
        tmapview.removeAllMarkerItem();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_home:
                setBt_home();
                break;
            case R.id.bt_findfac:
                tmapview.removeTMapPath();
                searchPOI(); // 마커
                break;
            case R.id.bt_find:
                isRun = true;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                findPath();
                myThread = (MyThread) new MyThread();
                myThread.execute();
                break;
            case R.id.bt_gps:
                /*  화면중심을 단말의 현재위치로 이동 */
                gpsview();
                break;
            case R.id.chatbot:
                Intent intent_chatbot = new Intent(getApplicationContext(), Chatbot_activity.class);
                startActivity(intent_chatbot);
                break;
            case R.id.bt_user:
                Intent intent_menu = new Intent(getApplicationContext(), UserActivity.class);
        }
    }
    // 현재 시간 받아오기
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("k시 mm분");
        String getTime = dateFormat.format(date);

        return getTime;
    }
    // 타이머
    public void startTimer() {
        // main 스레드에 핸들러 정의, 3rd 버튼 클릭하면 타이머가 시작됨.
        final int DIALOG_LOADING = 0;
        final int SHOW_LOADING =1;
        final int DISMISS_LOADING=2;

        Timer timer = new Timer();
        TimerTask task;
        Handler dialogHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case SHOW_LOADING:
                        Dialog();
                        //Log.d(TAG, "if문 들어가기 전" + flag_timer.get());
                }
            }
        };

        task = new TimerTask(){
            @Override
            public void run(){
                Message message = new Message();
                message.what=1;
                dialogHandler.sendMessage(message);
                flag_timer.set(0);
            }
        };
        Log.d(TAG,"i_pathtime : " + i_pathtime);
        timer.schedule(task, i_pathtime * 10000);
    }
    // 타이머 알림
    public TimerTask Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("예상 도착 시간 초과");
        builder.setMessage("예상 도착 시간을 초과하였습니다.");

        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isRun = false;
                flag_timer.set(1);
                Log.d(TAG, "타이머 응답 제발 plz plz : " + flag_timer.get());
                Toast.makeText(getApplicationContext(),"Clicked Yes",Toast.LENGTH_LONG);
                finish();
            }
        });
        builder.setNegativeButton("유지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag_timer.set(1);
                Toast.makeText(getApplicationContext(),"Clicked No", Toast.LENGTH_SHORT).show();
//                startTimer();
                dialog.cancel();
            }
        });
        AlertDialog alertD = builder.create();
        alertD.show();
        return null;
    }
    // 타이머 응답 메시지
    public void TimerResponse() {
        Log.d(TAG, "TimerResponse");
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "낙상 감지    "  + flag_naksang.get());
                Log.d(TAG, "이탈 감지    "  + flag_out.get());
                Log.d(TAG, "시간 오바    "  + flag_timer.get());

//                 타이머
                if (flag_timer.get() == 0){
                    isRun = false;
                    String message = "귀가 예상 시간이 초과한 지 5분이 지났습니다. 현재 " + userName + "님의 위치는 " +  address+ "입니다.";
                    Message(message);
                    flag_timer.set(1);
                }
                else if (flag_timer.get() == 1){
                    //Log.d(TAG, "10초 지나고 yes 버튼 누르고 나서 다시 0으로 바꿈" + flag_timer.get());
//                    flag_timer.set(1);
                }
//                 경로이탈
                if (flag_out.get() == 0){
                    isRun = false;
                    String message = "현재 경로이탈 했습니다. " + userName + "님의 위치는 " +  address+ "입니다.";;
                    Message(message);
                    flag_out.set(1);
                }
                else if (flag_out.get() == 1){
                    //Log.d(" ", "10초 지나고 yes 버튼 누르고 나서 다시 0으로 바꿈" + flag_out.get());
                }
                if (flag_naksang.get() == 0){
                    isRun = false;
                    String message = "[" + getTime() +  "]\n" + userName + "님이 " + address + "에서 낙상이 감지된 후 10분이 경과하였습니다.";
                    Message(message);
                    flag_naksang.set(1);
                }
                else if (flag_naksang.get() == 1){
                    //Log.d(TAG, "10초 지나고 yes 버튼 누르고 나서 다시 0으로 바꿈" + flag_naksang.get());
                }
            }
        };
        Timer mTimer = new Timer();
        mTimer.schedule(task, 300000);
    }
    private class MyThread extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... voids) {
            while(isRun){
                count = 0;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TimerResponse();
                mygpspoint = tmapgps.getLocation();

                lat = mygpspoint.getLatitude();
                lon = mygpspoint.getLongitude();

                tmapdata.convertGpsToAddress(startpoint.getLatitude(), startpoint.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String strAddress) {
                        address = strAddress;
                    }
                });

                String TAG = "";
                getDistance(lat, lon);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(count);
                Log.d(TAG, lat.toString() +"   "+ lon.toString()+"   "+ count);

                Log.d(TAG, Double.toString(flag_timer.get())+"  "+ Double.toString(flag_out.get()));

                if (isRun == false){
                    break;
                }
            }
            return count;
        }

        protected void onProgressUpdate(Integer... values){
//            System.out.println(values[0].intValue());
            startTimer();
            if (values[0].intValue() == 0){
                anomalyDetection();
                if (flag_out.get() == 0){
                    TimerResponse();
                }
            }
            if (flag_timer.get() == 0){
                TimerResponse();
            }
        }
        protected void onPostExecute(Integer result) {

        }

    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        if (!naksang) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:
//                objTV_X_Gyroscope.setText(("X : " + sensorEvent.values[0]));
//                objTV_Y_Gyroscope.setText(("Y : " + sensorEvent.values[1]));
//                objTV_Z_Gyroscope.setText(("Z : " + sensorEvent.values[2]));
                long currentTime = System.currentTimeMillis();
                long diff = (currentTime - lastTime);

                if (diff > 100) { //0.1초마다 센서의 값 변화를 감지
                    double x = sensorEvent.values[0];
                    double y = sensorEvent.values[1];
                    double z = sensorEvent.values[2];

                    abs = Math.sqrt(x * x + y * y + z * z);

                    // 낙상 감지
                    if (abs > SHAKE_THRESHOLD) {
                        flag_naksang.set(0);
                        lastTime = currentTime;
                        // 낙상 감지 알림
                        String title = "낙상 감지";
                        String message = "낙상이 감지되었습니다. 괜찮으신가요?";
                        String yesBtn = "예";
                        String noBtn = "";
                        Alert(title, message, yesBtn, noBtn);
                        TimerResponse();
                    }
                    break;
                }
//            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Call when sensor accuracy changed
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register listener for changing sensor value
        objSMG.registerListener(this, sensor_Gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Release sensor listener
        objSMG.unregisterListener(this);
    }
    // 푸시 알림 & 메시지
    public void Alert(String title, String message, String yesBtn, String noBtn) {
        // 낙상 감지 알림
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("낙상 감지");
        builder.setMessage("[" + getTime() + "] 낙상이 감지되었습니다. 괜찮으신가요?");
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yesBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flag_naksang.set(1);
                Log.d(TAG, "낙상 응답 제발 plz plz : " + flag_naksang.get());
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void Message(String message) {
        try {
            Log.d(TAG, "Message: " + parentsPhone);
            Log.d(TAG, "Message: " + message);
            // 전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(parentsPhone, null, message, null, null);
            Log.d(TAG, "Message: 전송완료");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}