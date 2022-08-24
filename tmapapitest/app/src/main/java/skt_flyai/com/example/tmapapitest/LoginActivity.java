package skt_flyai.com.example.tmapapitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private final  String TAG = getClass().getSimpleName();

    // server의 url을 적어준다
    private final String BASE_URL = "https://8237-39-115-190-39.jp.ngrok.io";
    private Interface mMyAPI;

    EditText ID, PW;
    Button LoginButton, RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ID = (EditText) findViewById(R.id.et_id);
        PW = (EditText) findViewById(R.id.et_pw);
        // 로그인 버튼
        LoginButton = (Button) findViewById(R.id.btn_login);
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
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
                if(response.isSuccessful()){
                    Toast.makeText((LoginActivity.this), "반갑습니다", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "권한이 없습니다", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<GetPost> call, Throwable t) {

            }
        });

    }
};

