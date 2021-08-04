package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.SpotifyApiEndpoints;
import com.sophiaxiang.wanderlust.databinding.FragmentEditSpotifySongBinding;
import com.sophiaxiang.wanderlust.models.AccessToken;
import com.sophiaxiang.wanderlust.models.SpotifyTrackModels.SpotifyTrack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditSpotifySongFragment extends Fragment {
    public static final String BASE_URL_TOKEN = "https://accounts.spotify.com/";
    public static final String TAG = "EditSpotifySongFragment";
    private final String clientId = "d6dbbace55c040ce944e523f2c970938";
    private final String clientSecret = "5a1329d147dd4e178c8e4ceb2227a743";
    private FragmentEditSpotifySongBinding mBinding;
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private String mAccessToken;
    private SpotifyApiEndpoints mApiService;
    private SpotifyTrack mTrack;

    public EditSpotifySongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_spotify_song, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setUpToolBar(view);
        setUpApiService();
        getAccessToken();

        mBinding.ibSearchSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mBinding.etSearchSong.getText().toString();
                if (url.equals("")) return;
                // get track id from url formatted as: https://open.spotify.com/track/0sY6ZUTh4yoctD8VIXz339?si=68588aa4d5234123
                String trackId = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
                getTrack(trackId);
            }
        });

        mBinding.btnSaveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(mFirebaseUser.getUid()).child("songAlbumCover").setValue(mTrack.getAlbum().getImages().get(0).getUrl());
                mDatabase.child("users").child(mFirebaseUser.getUid()).child("songName").setValue(mTrack.getName());
                mDatabase.child("users").child(mFirebaseUser.getUid()).child("songArtist").setValue(mTrack.getArtists().get(0).getName());
                mDatabase.child("users").child(mFirebaseUser.getUid()).child("songPreviewUrl").setValue(mTrack.getPreviewUrl())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Song saved!", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Set Spotify Song");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void setUpApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_TOKEN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = retrofit.create(SpotifyApiEndpoints.class);
    }

    private void getAccessToken() {
        String encodedString = encodeClientIDAndSecret();

        Call<AccessToken> call = mApiService.getToken("client_credentials", encodedString);
        call.enqueue(new retrofit2.Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    Log.d(TAG, "Access token value: " + accessToken.getAccessToken());
                    mAccessToken = accessToken.getAccessToken();
                } else {
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

    private void getTrack(String trackId) {
        Call<SpotifyTrack> call = mApiService.getTrack(trackId, "Bearer " + mAccessToken);
        call.enqueue(new Callback<SpotifyTrack>() {
            @Override
            public void onResponse(Call<SpotifyTrack> call, Response<SpotifyTrack> response) {
                int statusCode = response.code();
                mTrack = response.body();
                populateTrackViews();
            }

            @Override
            public void onFailure(Call<SpotifyTrack> call, Throwable t) {
                Log.e(TAG, "getTrack onFailure: " + t);
            }
        });
    }

    private void populateTrackViews() {
        Glide.with(getContext()).load(mTrack.getAlbum().getImages().get(0).getUrl()).placeholder(R.drawable.album_placeholder).into(mBinding.ivSelectedSongImage);
        mBinding.tvSelectedSongName.setText(mTrack.getName());
        mBinding.tvSelectedSongArtist.setText(mTrack.getArtists().get(0).getName());
        if (mTrack.getPreviewUrl() == null) {
            mBinding.tvNoPreviewUrl.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvNoPreviewUrl.setVisibility(View.INVISIBLE);
        }
    }

    private String encodeClientIDAndSecret() {
        String basic = "Basic ";
        String clientIDAndSecret = clientId + ":" + clientSecret;

        byte[] encodedValue = Base64.encode(clientIDAndSecret.getBytes(), Base64.NO_WRAP);
        String encodedString = new String(encodedValue);

        // The final output needs to have both the encoded String as well as 'Basic ' prepended to it
        return basic + encodedString;
    }
}