package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //로딩화면 시작.
        Loadingstart();
    }
    private void Loadingstart(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}

        //LoginButton = (Button) findViewById(R.id.btn_register);
//
        //LoginButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Intent intent = new Intent(getApplicationContext(), Home.class);
        //        startActivity(intent);
        //    }
        //});

//        textView = findViewById(R.id.et_id);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("") // 장고 서버 url
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        testApi testApi = retrofit.create(testAPi.class);
//
//        Call<List<Post>> call = testApi.getPosts();
//
//        call.enqueue(new Callback<List<Post>>() {
//            @Override
//            public void onResponse(Callback<List<Post>> call, Response<List<Post>> response) {
//
//                if (!response.isSuccessful())
//                {
//                    textView.setText("Code:" + response.code());
//                    return;
//                }
//
//                List<Post> posts = response.body();
//
//                for ( Post post : posts) {
//                    String content = "";
//                    content += "ID : " + post.getId() + "\n";
//                    content += "test : " + post.getTest() + "\n\n";
//
//                    textView.append(content);
//                }
//            }
//            @Override
//            public void onFailure(Call<List<Post>> call, Throwable t) {
//                textView.setText(t.getMessage());
//            }
//        });
