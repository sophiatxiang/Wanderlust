package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.sophiaxiang.wanderlust.R;

public class MyProfileFragment extends AbstractProfileFragment {

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    protected void setUpButtons() {
        super.setUpButtons();
        mBinding.fabLike.setVisibility(View.GONE);

        mBinding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditProfileFrag();
            }
        });

        mBinding.btnEditVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditVacationFrag();
            }
        });

        mBinding.rlMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditSpotifySongFrag();
            }
        });
    }

    protected void populateMusicViews() {
        if (mUser.getSongName() == null) {
            Glide.with(getContext()).load(R.drawable.album_placeholder).into(mBinding.ivAlbumCover);
            mBinding.tvSongName.setText("Add Song");
            return;
        }
        mBinding.tvSongName.setText(mUser.getSongName());
        mBinding.tvSongArtist.setText(mUser.getSongArtist());
        Glide.with(getContext()).load(mUser.getSongAlbumCover()).placeholder(R.drawable.album_placeholder).into(mBinding.ivAlbumCover);
    }

    private void goEditProfileFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current user", mUser);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void goEditVacationFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new MyVacationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("vacation", mVacation);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void goEditSpotifySongFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new EditSpotifySongFragment();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}