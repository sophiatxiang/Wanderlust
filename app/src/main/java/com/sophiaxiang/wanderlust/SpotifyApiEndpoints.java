package com.sophiaxiang.wanderlust;

import com.sophiaxiang.wanderlust.models.AccessToken;
import com.sophiaxiang.wanderlust.models.SpotifyTrackModels.SpotifyTrack;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SpotifyApiEndpoints {

    @FormUrlEncoded
    @POST("api/token")
    Call<AccessToken> getToken(@Field("grant_type") String body, @Header("Authorization") String header);

    @GET("https://api.spotify.com/v1/tracks/{id}")
    Call<SpotifyTrack> getTrack(@Path("id") String id, @Header("Authorization") String accessToken);
}
