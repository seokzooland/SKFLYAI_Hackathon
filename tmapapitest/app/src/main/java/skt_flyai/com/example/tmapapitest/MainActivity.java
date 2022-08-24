package skt_flyai.com.example.tmapapitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginButton = (Button) findViewById(R.id.btn_login);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

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
    }
}