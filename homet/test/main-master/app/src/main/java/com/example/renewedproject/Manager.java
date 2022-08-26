package com.example.renewedproject;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

//사용자가 데이터 또는 GPS를 활성화 했는지 체크하는 클래스
//아직 수정중
public class Manager extends AppCompatActivity {
    BaseApplication base;
    private Context mContext;
    LocationManager locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    TTSAdapter tts;

    Manager(Context c){
        this.mContext = c;
    }

    //GPS 활성화 체크
    public void checkingGPS() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //GPS 꺼져있거나, 데이터 꺼져있을 때
        if (!isGPSEnabled) {
            Log.d("GpsTracker", "GPS is not available");

            //base.progressOFF(); //혹시 로딩화면 있을 경우 끄게 함

            tts = TTSAdapter.getInstance(mContext);
            tts.speak("GPS를 켜주세요");

            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);

        }else return;
    }

        //데이터 활성화 확인
        public void checkingNetwork() {
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isNetworkEnabled) {
                Log.d("GpsTracker", "Network is not available");

                //base.progressOFF(); //혹시 로딩화면 있을 경우 끄게 함

                tts = TTSAdapter.getInstance(mContext);
                tts.speak("데이터를 켜주세요");

                //데이터 설정화면으로 이동
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
        }else return;
    }
}
