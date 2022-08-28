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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener {

    String API_KEY = "l7xxfdc4f7caa4784dab9cc0280d386ed572";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

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
    // Marker
    private Bitmap bitmap;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();

    private String address;
    private Double lat = null;
    private Double lon = null;

    private ImageButton bt_find; //주소로 경로 버튼
    boolean find_mode = false;
    private ImageButton bt_fac;  //주변 안전지킴이 찾기 버튼
    boolean fac_mode = false;
    private ImageButton bt_home; //홈
    boolean home_mode = true    ;
    private ImageButton bt_gps;
    boolean gps_mode = false;
    private ImageButton bt_Chatbot; // 챗봇버튼

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        setTMapAuth(); //Tmap 각종 객체 선언
        setGPS(); // GPS 설정
        out_detect();
        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        //버튼 선언
        bt_home = (ImageButton) findViewById(R.id.bt_home);
        bt_fac = (ImageButton) findViewById(R.id.bt_findfac);
        bt_find = (ImageButton) findViewById(R.id.bt_find);
        bt_gps = (ImageButton) findViewById(R.id.bt_gps);
        bt_Chatbot = (ImageButton) findViewById(R.id.chatbot);

        bt_gps.bringToFront();
        bt_Chatbot.bringToFront();
        //버튼 리스너 등록
        bt_fac.setOnClickListener(this);
        bt_find.setOnClickListener(this);
        bt_home.setOnClickListener(this);
        bt_gps.setOnClickListener(this);
        bt_Chatbot.setOnClickListener(this);
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
        tmapgps.setMinTime(1000); //위치변경 인식 최소시간설정
        tmapgps.setMinDistance(5); //위치변경 인식 최소거리설정
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
                ActivityCompat.requestPermissions(Home.this, REQUIRED_PERMISSIONS,
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

        bt_home.setImageResource(R.drawable.home_off);
        bt_find.setImageResource(R.drawable.find_on);
        bt_fac.setImageResource(R.drawable.poi);

        TMapPoint startpoint = tmapgps.getLocation(); // 입력으로 수정해야함
        TMapPoint endpoint = new TMapPoint(37.510350, 127.066847); // 입력으로 수정해야함

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
                    int i_pathtime = Integer.parseInt(s_pathtime);
                    System.out.println(i_pathtime / 60);
                }
            }
        });
        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                        if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                            String test = nodeListPlacemarkItem.item(j).getTextContent().trim();
                            System.out.println(test);
                        }
                    }
                }
            }
        });


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

    private void out_detect() {
        TMapPoint startpoint = tmapgps.getLocation(); // 입력으로 수정해야함
        TMapPoint endpoint = new TMapPoint(37.510350, 127.066847); // 입력으로 수정해야함

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
                findPath(); // 보행자 경로 탐색
                break;
            case R.id.bt_gps:
                /*  화면중심을 단말의 현재위치로 이동 */
                gpsview();
                break;
            case R.id.chatbot:
                Intent intent_chatbot = new Intent(getApplicationContext(), Chatbot_activity.class);
                startActivity(intent_chatbot);
                break;
        }

    }
}

