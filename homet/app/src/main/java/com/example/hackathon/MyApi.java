//package com.example.hackathon;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.DELETE;
//import retrofit2.http.GET;
//import retrofit2.http.Multipart;
//import retrofit2.http.PATCH;
//import retrofit2.http.POST;
//import retrofit2.http.Part;
//import retrofit2.http.Path;
//
//public interface MyAPI{
//
//    @Multipart
//    @POST("/accounts/")
//    Call<ResponseBody> post_accounts(
//            @Part("identify") RequestBody param1,
//            @Part("password") RequestBody param2
//    );
//
//    @POST("/posts/")
//    Call<PostItem> post_posts(@Body PostItem post);
//
//    @PATCH("/posts/{pk}/")
//    Call<PostItem> patch_posts(@Path("pk") int pk, @Body PostItem post);
//
//    @DELETE("/posts/{pk}/")
//    Call<PostItem> delete_posts(@Path("pk") int pk);
//
//    @GET("/posts/")
//    Call<List<PostItem>> get_posts();
//
//    @GET("/posts/{pk}/")
//    Call<PostItem> get_post_pk(@Path("pk") int pk);
//}