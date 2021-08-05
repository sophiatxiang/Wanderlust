package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractProfileFragment extends Fragment {
    public static final String TAG = "AbstractProfileFragment";
    protected FragmentProfileBinding mBinding;
    protected DatabaseReference mDatabase;
    protected DatabaseReference mCurrentUserNodeReference;
    protected DatabaseReference mVacationDetailsReference;
    protected User mUser;
    protected Vacation mVacation;
    protected List<String> mUserImages;
    protected int mPosition;
    protected String mCurrentUserId;
    protected List<TextView> mAttractionViews;
    protected List<ImageView> mBullets;
    protected MediaPlayer mMediaPlayer;

    public AbstractProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        mUser = (User) bundle.getSerializable("user");
        mVacation = mUser.getVacation();

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUserNodeReference = mDatabase.child("users").child(mUser.getUserId());
        mVacationDetailsReference = mDatabase.child("users").child(mUser.getUserId()).child("vacation");

        mAttractionViews = Arrays.asList(mBinding.tvAttraction1, mBinding.tvAttraction2,
                mBinding.tvAttraction3, mBinding.tvAttraction4, mBinding.tvAttraction5);
        mBullets = Arrays.asList(mBinding.ivBullet1, mBinding.ivBullet2, mBinding.ivBullet3,
                mBinding.ivBullet4, mBinding.ivBullet5);

        mUserImages = new ArrayList<>();
        mPosition = 0;
        populateImageList();

        setUpToolBar(view);
        setUpButtons();
        setUpMap();
        setUpMediaPlayer();

        populateProfileViews();
        populateVacationViews();
        populateMusicViews();

        setProfileInfoListener();
        setVacationInfoListener();
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        super.onPause();
    }

    protected void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    protected void setUpButtons() {
        mBinding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition > 0) mPosition--;
                if (mUserImages.get(mPosition) != null) {
                    Glide.with(mBinding.ivPhoto.getContext())
                            .load(Uri.parse(mUserImages.get(mPosition)))
                            .into(mBinding.ivPhoto);
                }
            }
        });

        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition < mUserImages.size() - 1) mPosition++;
                if (mUserImages.get(mPosition) != null) {
                    Glide.with(mBinding.ivPhoto.getContext())
                            .load(Uri.parse(mUserImages.get(mPosition)))
                            .into(mBinding.ivPhoto);
                }
            }
        });

        mBinding.fabInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser.getInstagram().equals("")) {
                    Toast.makeText(getContext(), "Please provide youR Instagram username!", Toast.LENGTH_SHORT).show();
                } else {
                    Uri appUri = Uri.parse("https://instagram.com/_u/" + mUser.getInstagram());
                    Uri browserUri = Uri.parse("https://instagram.com/" + mUser.getInstagram());

                    Intent appIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    if (appIntent != null) {
                        appIntent.setAction(Intent.ACTION_VIEW);
                        appIntent.setData(appUri);
                        startActivity(appIntent);
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                        startActivity(browserIntent);
                    }
                }
            }
        });

        mBinding.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapDialog();
            }
        });

        mBinding.ivAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    mBinding.ivPlayPause.setImageResource(R.drawable.play_icon);
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                } else {
                    mBinding.ivPlayPause.setImageResource(R.drawable.pause_icon);
                    playAudio();
                }
            }
        });
    }

    protected void setUpMap() {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + mUser.getVacation().getLatitude() + "," +
                mUser.getVacation().getLongitude() + "&zoom=14&size=363x220&scale=2&markers=color:red%7Clabel:%7C11211%7C11206%7C11222&key=AIzaSyDtWeLjyQ4g1uU9oow9HDjBX7T0AjPc9pQ";
        Glide.with(getContext()).load(url).centerCrop().into(mBinding.ivMap);
    }

    protected void setUpMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.reset();
            }
        });
    }

    protected void setVacationInfoListener() {
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Vacation object and use the values to update the UI
                mVacation = dataSnapshot.getValue(Vacation.class);
                populateVacationViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        mVacationDetailsReference.addValueEventListener(vacationListener);
    }

    protected void setProfileInfoListener() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                mUser = dataSnapshot.getValue(User.class);
                populateProfileViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        mCurrentUserNodeReference.addValueEventListener(userListener);
    }

    protected void populateProfileViews() {
        mBinding.tvNameAge.setText(mUser.getName() + ", " + mUser.getAge());
        mBinding.tvBio.setText(mUser.getBio());
        mBinding.tvFrom.setText(mUser.getFrom());
        mBinding.tvAdventureLevel.setText(mUser.getAdventureLevel() + "/5");
        populateImageList();
    }

    // fills ArrayList with Uri's for the user's images
    protected void populateImageList() {
        mUserImages.clear();
        mUserImages.add(mUser.getImage1());
        mUserImages.add(mUser.getImage2());
        mUserImages.add(mUser.getImage3());
        populateImageView();
    }

    protected void populateImageView() {
        if (mUser.getImage1() == null) {
            mBinding.ivPhoto.setImageResource(R.drawable.add_image);
        } else {
            mPosition = 0;
            Glide.with(mBinding.ivPhoto.getContext())
                    .load(Uri.parse(mUserImages.get(mPosition)))
                    .into(mBinding.ivPhoto);
        }
    }

    protected void populateVacationViews() {
        if (!mVacation.getDestination().equals("")) {
            mBinding.tvLocationDate.setText(mVacation.getDestination() + "   |   " + mVacation.getStartDate() + " - " + mVacation.getEndDate());
        } else mBinding.tvLocationDate.setText("No vacation details yet");

        if (!mVacation.getNotes().equals("")) {
            mBinding.tvVacationNotes.setText(mVacation.getNotes());
        } else mBinding.tvVacationNotes.setText("None");

        if (mVacation.getAttraction1() != null) {
            mBinding.tvAttractionsHeader.setVisibility(View.VISIBLE);
            mBinding.tvAttractionsHeader.setVisibility(View.VISIBLE);
            displayAttraction(0, mVacation.getAttraction1());
        }
        if (mVacation.getAttraction2() != null) {
            displayAttraction(1, mVacation.getAttraction2());
        }
        if (mVacation.getAttraction3() != null) {
            displayAttraction(2, mVacation.getAttraction3());
        }
        if (mVacation.getAttraction4() != null) {
            displayAttraction(3, mVacation.getAttraction4());
        }
        if (mVacation.getAttraction5() != null) {
            displayAttraction(4, mVacation.getAttraction5());
        }
    }

    protected void displayAttraction(int i, String attraction) {
        mBullets.get(i).setVisibility(View.VISIBLE);
        mAttractionViews.get(i).setVisibility(View.VISIBLE);
        mAttractionViews.get(i).setText(attraction);
    }

    protected void showMapDialog() {
        FragmentManager fm = getChildFragmentManager();
        MapDialogFragment mapDialog = MapDialogFragment.newInstance(mUser.getVacation().getDestination(), mUser.getVacation().getLatitude(), mUser.getVacation().getLongitude());
        mapDialog.show(fm, "fragment_map_dialog");
    }

    protected void playAudio() {
        try {
            if (mUser.getSongPreviewUrl() == null) {
                Toast.makeText(getContext(), "This song does not have a preview", Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaPlayer.setDataSource(mUser.getSongPreviewUrl());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void populateMusicViews();
}