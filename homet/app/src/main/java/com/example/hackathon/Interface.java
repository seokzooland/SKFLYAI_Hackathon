package com.example.hackathon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Interface
{
    @POST("accounts/acclist/")
    Call<GetPost> posts(@Body GetPost post );

    @GET("/check/")
    Call<GetPost> gets(@Query("userID") String userID);

    @GET("accounts/acclist/{userID}")
    Call<GetPost> gets_accounts(@Path("userID") String userID);

    @GET("accounts/accpwcheck/{userID}/")
    Call<GetPost> gets_accounts_login(@Path("userID") String userID, @Query("search") String userPW);
}
