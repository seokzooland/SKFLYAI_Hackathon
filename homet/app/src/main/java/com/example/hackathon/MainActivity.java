package com.example.hackathon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hackathon.Interface;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    final int MULTI_PERMISSION = 2000;
    ArrayList<String> permissionNoRealTime = new ArrayList(); //실시간 퍼미션 거부된 값을 담는다
    String[] permissionArray = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
    };
    private final  String TAG = getClass().getSimpleName();

    // server의 url을 적어준다
    private final String BASE_URL = "http://20.249.89.149:8000";
    private Interface mMyAPI;

    EditText ID, PW;
    Button LoginButton, RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getPermissionCheck();


        ID = (EditText) findViewById(R.id.et_id);
        PW = (EditText) findViewById(R.id.et_pw);

        ID.setText("xodn");
        PW.setText("1234");
        // 로그인 버튼
        LoginButton = (Button) findViewById(R.id.btn_register);
        // 회원가입 버튼
        RegisterButton = (Button)findViewById(R.id.btn_goRegister);

        initMyAPI(BASE_URL);

        RegisterButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 회원가입 버튼 클릭
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMe();
            }
        });
    }

    private void initMyAPI(String baseUrl) {
        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(Interface.class);
    }

    private void loginMe() {
        String userID = ID.getText().toString();
        String userPW = PW.getText().toString();
        Call<GetPost> getCall = mMyAPI.gets_accounts_login(userID, userPW);

        getCall.enqueue(new Callback<GetPost>() {
            @Override
            public void onResponse(Call<GetPost> call, Response<GetPost> response) {
                if( response.isSuccessful()){
                    String json = new Gson().toJson(response.body());
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(json);

                    String userID = element.getAsJsonObject().get("userID").getAsString();
                    String userName = element.getAsJsonObject().get("userName").getAsString();
                    String userAddress = element.getAsJsonObject().get("userAddress").getAsString();
                    String parentsPhone = element.getAsJsonObject().get("parentsPhone").getAsString();

                    Toast.makeText((MainActivity.this), userName + "님 반갑습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Home.class);

                    intent.putExtra("userID", userID);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userAddress", userAddress);
                    intent.putExtra("parentsPhone", parentsPhone);

                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "권한이 없습니다", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GetPost> call, Throwable t) {

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTI_PERMISSION: {
                if (grantResults.length > 0) { //퍼미션 권한이 부여되지 않는 배열 길이가 0보다 클경우
                    if (permissionNoRealTime.size() > 0) {
                        permissionNoRealTime.clear();
                    }
                    for (int i = 0; i < permissions.length; i++) { //배열을 순회하면서
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { //권한이 부여되어 있지 않을 경우 확인
                            permissionNoRealTime.add(permissions[i]);
                        }
                    }
                    Log.d("---", "---");
                    Log.e("//===========//", "============");
                    Log.d("", "\n" + "[실시간 퍼미션 거부된 리스트 : " + permissionNoRealTime.toString() + "]");
                    Log.e("//===========//", "============");
                    Log.d("---", "---");
                    if (permissionNoRealTime.size() > 0) { //TODO 실시간으로 권한 허용이 거부된 값이 있을 경우
                        String Tittle = "퍼미션 허용 확인";
                        String Message = "퍼미션을 허용해야 정상 이용가능합니다" + "\n"
                                + "[설정>권한]에서 퍼미션을 허용하시거나, " + "\n"
                                + "앱을 재실행해 권한을 허용해주세요";
                        String buttonNo = "종 료";
                        String buttonYes = "설 정";
                        new AlertDialog.Builder(this)
                                .setTitle(Tittle) //팝업창 타이틀 지정
                                //.setIcon(R.drawable.ic_launcher_foreground) //팝업창 아이콘 지정
                                .setMessage(Message) //팝업창 내용 지정
                                .setCancelable(false) //외부 레이아웃 클릭시도 팝업창이 사라지지않게 설정
                                .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        /** ==== [애플리케이션 정보 창 이동하기] ==== */
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                    }
                                })
                                .setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        try {
                                            finishAffinity();
                                            overridePendingTransition(0, 0);
                                            //android.os.Process.killProcess(android.os.Process.myPid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .show();
                    } else { //TODO 실시간으로 모든 권한이 허용된 경우 [메소드 호출]
                        getPermissionOK(); //메소드 호출
                    }
                }
                return;
            }
        }
    }
    //TODO ====== 정상 모든 퍼미션 허용 시 수행 메소드 ======/
    public void getPermissionOK(){
        try {
            //TODO ==== 내용 작성 ====

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //TODO ====== 다중 퍼미션 확인 메소드 ======/
    public void getPermissionCheck(){
        try {
            ArrayList <String> permissionOK = new ArrayList(); //퍼미션 허용된 값을 담는다
            ArrayList <String> permissionNO = new ArrayList(); //퍼미션 거부된 값을 담는다
            if(permissionArray.length > 0){
                int check = 0;
                for (String data : permissionArray) { //매니페스트에 등록된 허용받을 퍼미션이 허용되었는지 확인한다
                    check = ContextCompat.checkSelfPermission(this, data);
                    if (check != PackageManager.PERMISSION_GRANTED) { //권한이 부여되지 않았을 경우
                        permissionNO.add(data);
                    }
                    else { //퍼미션 권한이 부여 되었을 경우
                        permissionOK.add(data);
                    }
                }
                Log.d("---","---");
                Log.w("//===========//","============");
                Log.d("","\n"+"[퍼미션 허용된 리스트 : "+permissionOK.toString()+"]");
                Log.d("","\n"+"[퍼미션 거부된 리스트 : "+permissionNO.toString()+"]");
                Log.w("//===========//","============");
                Log.d("---","---");
                if(permissionNO.size() > 0){ //TODO 퍼미션 거부된 값이 있을 경우
                    ActivityCompat.requestPermissions(this, permissionNO.toArray(new String[permissionNO.size()]), MULTI_PERMISSION);
                }
                else { //TODO 모든 퍼미션이 허용된 경우 [메소드 호출]
                    //Toast.makeText(getApplication(),"모든 퍼미션이 허용되었습니다 !!! ",Toast.LENGTH_SHORT).show();
                    getPermissionOK(); //메소드 호출
                }
            }
            else {
                Toast.makeText(getApplication(),"퍼미션 허용을 확인할 데이터가 없습니다 ... ",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
};
