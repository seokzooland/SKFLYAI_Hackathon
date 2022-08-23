package com.example.testapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @POST("/posts/")
    Call<Post> post_posts(@Body Post post);

    @PATCH("/posts/{pk}/")
    Call<Post> patch_posts(@Path("pk") int pk, @Body Post post);

    @DELETE("/posts/{pk}/")
    Call<Post> delete_posts(@Path("pk") int pk);

    @GET("posts")
    Call<List<Post>> get_posts();

    @GET("/posts/{pk}/")
    Call<Post> get_post_pk(@Path("pk") int pk);
}
