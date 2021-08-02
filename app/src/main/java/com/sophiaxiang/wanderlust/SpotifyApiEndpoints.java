package com.sophiaxiang.wanderlust;

import com.sophiaxiang.wanderlust.models.AccessToken;

import retrofit.http.GET;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SpotifyApiEndpoints {

    @FormUrlEncoded
    @POST("api/token")
    Call<AccessToken> getToken(@Field("grant_type") String body, @Header("Authorization") String header);
}
