package skt_flyai.com.example.tmapapitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity{
    private final  String TAG = getClass().getSimpleName();
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    // server의 url을 적어준다
    private final String BASE_URL = "https://5450-2001-e60-8753-a52f-45ad-2ab8-d938-98d4.jp.ngrok.io";
    private Interface mMyAPI;

    private EditText ID, PW, PWCheck, Name, Year, Month, Day, Address;
    private Button IDCheckButton, RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 회원가입 항목
        ID = findViewById(R.id.et_id);
        PW = findViewById(R.id.et_pw);
        PWCheck = findViewById(R.id.et_pwck);
        Name = findViewById(R.id.et_name);
        Year = findViewById(R.id.et_year);
        Month = findViewById(R.id.et_month);
        Day = findViewById(R.id.et_day);
        Address = findViewById(R.id.et_address);

        // address 터치 안되게 막기
        Address.setFocusable(false);



        RegisterButton = findViewById(R.id.btn_register);
        IDCheckButton = findViewById(R.id.btn_idck);

        initMyAPI(BASE_URL);

        IDCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"중복 확인");
                String userID = ID.getText().toString();

//                GetPost item = new GetPost();
//                item.postID(userID);

                Call<GetPost> getCall = mMyAPI.gets_accounts(userID);

                getCall.enqueue(new Callback<GetPost>() {
                    @Override
                    public void onResponse(Call<GetPost> call, Response<GetPost> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText((RegisterActivity.this), "이미 사용 중인 아이디입니다.", Toast.LENGTH_LONG).show();
                        } else {
                                Toast.makeText(RegisterActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Status Code : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<GetPost> call, Throwable t) {
                        Log.d(TAG, "Fail msg : " + t.getMessage());
                    }
                });
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(), "버튼 클릭");
                registerMe();
//
            }
        });

        Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    Log.i("주소설정페이지", "주소입력창 클릭");
                    Intent i = new Intent(getApplicationContext(), AddressActivity.class);
                    // 화면전환 애니메이션 없애기
                    overridePendingTransition(0, 0);
                    // 주소결과
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);

                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        Log.i("test", "data:" + data);
                        Address.setText(data);
                    }
                }
                break;
        }
    }

    private void initMyAPI(String baseUrl){

        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(Interface.class);
    }

    private void registerMe()
    {
        String userID = ID.getText().toString();
        String userPW = PW.getText().toString();
        String userName = Name.getText().toString();
        int userBirth = Integer.parseInt(Year.getText().toString() + Month.getText().toString() + Day.getText().toString());
        String userAddress = Address.getText().toString();

        String pwcheck = PWCheck.getText().toString();

        if (!userPW.equals(pwcheck)) {
            Toast.makeText(RegisterActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
        }

        GetPost item = new GetPost();
        item.postID(userID);
        item.postPW(userPW);
        item.postName(userName);
        item.postBirth(userBirth);
        item.postAddress(userAddress);

        Call<GetPost> postCall = mMyAPI.posts(item);

        postCall.enqueue(new Callback<GetPost>()
        {
            @Override
            public void onResponse(Call<GetPost> call, Response<GetPost> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "회원가입 완료", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Log.d(TAG,"등록 완료");
                }else {
                    Log.d(TAG,"Status Code : " + response.code());
                    Toast.makeText(RegisterActivity.this, "아이디 중복을 확인해주세요.", Toast.LENGTH_LONG).show();
                    try {
                        Log.d(TAG,response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,call.request().body().toString());
                }
            }

            @Override
            public void onFailure(Call<GetPost> call, Throwable t) {
                Log.d(TAG,"Fail msg : " + t.getMessage());
            }
        });
    }
}
