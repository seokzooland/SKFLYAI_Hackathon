package com.example.testapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.view.View;

import com.example.testapp.databinding.ActivityMainBinding;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final  String TAG = getClass().getSimpleName();

    // server의 url을 적어준다
    private final String BASE_URL = "http://10.0.2.2:8000/";
    private JsonPlaceHolderApi mMyAPI;

    private TextView mListTv;

    private Button mGetButton;
    private Button mPostButton;
    private Button mPatchButton;
    private Button mDeleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListTv = findViewById(R.id.result1);

        mGetButton = findViewById(R.id.button1);
        mGetButton.setOnClickListener(this);
        mPostButton = findViewById(R.id.button2);
        mPostButton.setOnClickListener(this);
        mPatchButton = findViewById(R.id.button3);
        mPatchButton.setOnClickListener(this);
        mDeleteButton = findViewById(R.id.button4);
        mDeleteButton.setOnClickListener(this);

        initMyAPI(BASE_URL);
    }

    private void initMyAPI(String baseUrl){

        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(JsonPlaceHolderApi.class);
    }


    @Override
    public void onClick(View v) {
        if( v == mGetButton){
            Log.d(TAG,"GET");
            Call<List<Post>> getCall = mMyAPI.get_posts();
            getCall.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if( response.isSuccessful()){
                        List<Post> mList = response.body();
                        String result ="";
                        for( Post item : mList){
                            result += "title : " + item.getTitle() + " text: " + item.getText() + "\n";
                        }
                        mListTv.setText(result);
                    }else {
                        Log.d(TAG,"Status Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Log.d(TAG,"Fail msg : " + t.getMessage());
                }
            });
        }else if(v == mPostButton){
            Log.d(TAG,"POST");


            Post item = new Post();
            item.setTitle("new title");
            item.setText("new text");
            Call<Post> postCall = mMyAPI.post_posts(item);
            postCall.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG,"등록 완료");
                    }else {
                        Log.d(TAG,"Status Code : " + response.code());
                        try {
                            Log.d(TAG,response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,call.request().body().toString());
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d(TAG,"Fail msg : " + t.getMessage());
                }
            });
        }else if( v == mPatchButton){
            Log.d(TAG,"PATCH");
            Post item = new Post();
            item.setTitle("android patch title");
            item.setText("android patch text");
            //pk 값은 임의로 하드코딩하였지만 동적으로 setting 해서 사용가능
            Call<Post> patchCall = mMyAPI.patch_posts(1,item);
            patchCall.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG,"patch 성공");
                    }else{
                        Log.d(TAG,"Status Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d(TAG,"Fail msg : " + t.getMessage());
                }
            });


        }else if( v == mDeleteButton){
            Log.d(TAG,"DELETE");
            // pk 값은 임의로 변경가능
            Call<Post> deleteCall = mMyAPI.delete_posts(1);
            deleteCall.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(response.isSuccessful()){
                        Log.d(TAG,"삭제 완료");
                    }else {
                        Log.d(TAG,"Status Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.d(TAG,"Fail msg : " + t.getMessage());
                }
            });
        }
    }
}