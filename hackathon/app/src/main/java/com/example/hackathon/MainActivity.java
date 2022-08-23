package com.example.hackathon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, View.OnClickListener {

    String API_KEY = "l7xxfdc4f7caa4784dab9cc0280d386ed572";

    // T Map Tracking Mode
    private boolean m_bTrackingMode = true;
    // T Map View
    private TMapView tmapview = null;

    private static int mMarkerID;

    // POI 명칭
    private ArrayList<String> arrBuilding = new ArrayList<>();
    // T Map Data
    private TMapData tmapdata = null;

    // T Map GPS
    private TMapGpsManager tmapgps = null;


    // Marker
    private Bitmap bitmap;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    private String address;
    private Double lat = null;
    private Double lon = null;

    private Button bt_find; //주소로 경로 버튼
    private Button bt_fac;  //주변 안전지킴이 찾기 버튼
    private Button bt_home; //홈
    private Button bt_gps;

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTMapAuth(); //Tmap 각종 객체 선언
        setGPS(); // GPS 설정
        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        //버튼 선언
        bt_home = (Button) findViewById(R.id.bt_home);
        bt_fac = (Button) findViewById(R.id.bt_findfac);
        bt_find = (Button) findViewById(R.id.bt_find) ;
        bt_gps = (Button) findViewById(R.id.bt_gps);

        //버튼 리스너 등록
        bt_fac.setOnClickListener(this);
        bt_find.setOnClickListener(this);
        bt_home.setOnClickListener(this);
        bt_gps.setOnClickListener(this);

    }

    private void setTMapAuth()
    {
        tmapdata = new TMapData(); //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapview);
        tmapview = new TMapView(this);

        linearLayout.addView(tmapview);
        
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

    private void setGPS(){
        tmapgps = new TMapGpsManager(MainActivity.this); //단말의 위치탐색을 위한 클래스
        tmapgps.setMinTime(1000); //위치변경 인식 최소시간설정
        tmapgps.setMinDistance(5); //위치변경 인식 최소거리설정
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //네트워크 기반의 위치탐색
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //위성기반의 위치탐색
        tmapgps.OpenGps();


    };

    private void findPath(){
        TMapPoint startpoint = new TMapPoint(37.5248, 127.05); // 입력으로 수정해야함
        TMapPoint endpoint = new TMapPoint(37.510350, 127.066847); // 입력으로 수정해야함

        // 보행자 경로 탐색
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                tmapview.addTMapPath(polyLine);
            }
        });
    }

    private void searchPOI() {
        final TMapData tMapData = new TMapData();
        final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
        final ArrayList<String> arrTitle = new ArrayList<>();
        final ArrayList<String> arrAddress = new ArrayList<>();
        TMapPoint point = tmapview.getCenterPoint();

        tMapData.findAroundNamePOI(point, "편의점;경찰서", 1, 30, new TMapData.FindAroundNamePOIListenerCallback() {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                for(int i = 0; i < poiItem.size(); i++ ){
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
                                 ArrayList<String> arrAddress)
    {
        for( int i = 0; i < arrTPoint.size(); i++ )
        {
            Bitmap bitmapIcon = createMarkerIcon(R.drawable.ping);

            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
            tMapMarkerItem.setIcon(bitmapIcon);

            tMapMarkerItem.setTMapPoint(arrTPoint.get(i));

            tmapview.addMarkerItem("markerItem" + i, tMapMarkerItem);

            setBalloonView(tMapMarkerItem, arrTitle.get(i), arrAddress.get(i));
        }
    }
    private void setBalloonView(TMapMarkerItem marker, String title, String address)
    {
        marker.setCanShowCallout(true);

        if( marker.getCanShowCallout() )
        {
            marker.setCalloutTitle(title);
            marker.setCalloutSubTitle(address);
        }
    }
    private Bitmap createMarkerIcon(int image)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                image);
        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50,false);

        return bitmap;
    }
    private void gpsview(){
        TMapPoint tpoint = tmapgps.getLocation();
        double Latitude = tpoint.getLatitude();
        double Longitude = tpoint.getLongitude();
        tmapview.setLocationPoint(Latitude, Longitude);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bt_home:
                tmapview.removeAllMarkerItem();
                tmapview.removeTMapPath();
                break;
            case R.id.bt_findfac:
                searchPOI();
                break;
            case R.id.bt_find:
                findPath(); // 보행자 경로 탐색
                break;
            case R.id.bt_gps:
                /*  화면중심을 단말의 현재위치로 이동 */
                gpsview();
                break;
            }
        }
}

