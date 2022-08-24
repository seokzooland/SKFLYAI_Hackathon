package skt_flyai.com.example.tmapapitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity{
    private final  String TAG = getClass().getSimpleName();

    // server의 url을 적어준다
    private final String BASE_URL = "https://8237-39-115-190-39.jp.ngrok.io";
    private Interface mMyAPI;

    private EditText ID, PW, PWCheck, Name, Year, Month, Day;
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

        RegisterButton = findViewById(R.id.btn_login);
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

//                                Toast.makeText(RegisterActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_LONG).show();
//                            } else {
//                                Boolean TF = false;
//                                Toast.makeText((RegisterActivity.this), "이미 사용 중인 아이디입니다.", Toast.LENGTH_LONG).show();
//                            }

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

        String pwcheck = PWCheck.getText().toString();

        if (!userPW.equals(pwcheck)) {
            Toast.makeText(RegisterActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
        }

        GetPost item = new GetPost();
        item.postID(userID);
        item.postPW(userPW);
        item.postName(userName);
        item.postBirth(userBirth);

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
