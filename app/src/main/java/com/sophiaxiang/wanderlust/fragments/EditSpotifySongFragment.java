package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.SpotifyApiEndpoints;
import com.sophiaxiang.wanderlust.models.AccessToken;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import okhttp3.Callback;
import okhttp3.Headers;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditSpotifySongFragment extends Fragment {
    public static final String BASE_URL = "https://accounts.spotify.com/";
    public static final String TAG = "EditSpotifySongFragment";
    private String clientId = "d6dbbace55c040ce944e523f2c970938";
    // TODO: client secret
    private String clientSecret = "CLIENT SECRET";
    private String mAccessToken;

    public EditSpotifySongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_spotify_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyApiEndpoints apiService = retrofit.create(SpotifyApiEndpoints.class);

        // client id and client secret encoded in base 64
        String encodedString = encodeClientIDAndSecret();

        Call<AccessToken> call = apiService.getToken("client_credentials", encodedString);
        call.enqueue(new retrofit2.Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    Log.d(TAG, "Access token value: " + accessToken.getAccessToken());
                    mAccessToken = accessToken.getAccessToken();
                }
                else {
                    Log.d(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d(TAG, "getAccessToken onFailure:" + call.request().toString());
                mAccessToken = "";
            }
        });
    }

    private String encodeClientIDAndSecret(){
        String basic = "Basic ";
        String clientIDAndSecret = clientId + ":" + "clientSecret";

        byte [] encodedValue = Base64.encode(clientIDAndSecret.getBytes(), Base64.NO_WRAP);
        String encodedString = new String(encodedValue);

        // The final output needs to have both the encoded String as well as 'Basic ' prepended to it
        return basic + encodedString;
    }
}