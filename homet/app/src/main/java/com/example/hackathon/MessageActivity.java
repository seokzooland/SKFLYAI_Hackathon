package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity {

    private EditText Phone;
    private Button SMS;

    private final int SMS_RECEIVE_PERMISSON = 1;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

//    public void checkPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_RECEIVE_PERMISSON);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_RECEIVE_PERMISSON);
//        }
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Phone = findViewById(R.id.et_phone);
        SMS = findViewById(R.id.btn_sendSMS);

        Phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 실행 함수
                String phoneNo = Phone.getText().toString();     // 보호자 휴대폰번호 정보
                String message = "MapSosa App : 낙상감지가 발생하였습니다!";
//                        + "\n위도 : " + latitude
//                        + "\n경도 : " + longtide;

//                try {
                    // 전송
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "SMS 전송 실패", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
            }
        });
    }



}