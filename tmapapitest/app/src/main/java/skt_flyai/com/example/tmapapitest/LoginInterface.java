package skt_flyai.com.example.tmapapitest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface
{
//    String LOGIN_URL = "http://xx.xxx.xxx.xx/";

    @FormUrlEncoded
    @POST("")   // 값 넘겨줄 django file 을 쓰는 건가?
    Call<String> getUserLogin(
            @Field("id") String id,
            @Field("pw") String pw
    );
}
