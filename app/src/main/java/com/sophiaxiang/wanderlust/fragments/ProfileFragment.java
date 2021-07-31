package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private FragmentProfileBinding mBinding;
    private DatabaseReference mDatabase;
    private DatabaseReference mCurrentUserNodeReference;
    private DatabaseReference mVacationDetailsReference;
    private String currentUserId;
    private User mCurrentUser;
    private Vacation mVacation;
    private List<String> mCurrentUserImages;
    private int mPosition;
    private List<TextView> mAttractionViews;
    private List<ImageView> mBullets;

    public ProfileFragment() {
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

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUserNodeReference = mDatabase.child("users").child(currentUserId);
        mVacationDetailsReference = mDatabase.child("users").child(currentUserId).child("vacation");

        Bundle bundle = getArguments();
        mCurrentUser = (User) bundle.getSerializable("current user");
        mVacation = (Vacation) bundle.getSerializable("vacation");

        mAttractionViews = Arrays.asList(mBinding.tvAttraction1, mBinding.tvAttraction2,
                mBinding.tvAttraction3, mBinding.tvAttraction4, mBinding.tvAttraction5);
        mBullets = Arrays.asList(mBinding.ivBullet1, mBinding.ivBullet2, mBinding.ivBullet3,
                mBinding.ivBullet4, mBinding.ivBullet5);

        mCurrentUserImages = new ArrayList<>();
        mPosition = 0;
        populateImageList();

        setUpToolBar(view);
        setUpButtons();

        populateProfileViews();
        populateVacationViews();

        setProfileInfoListener();
        setVacationInfoListener();
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUpButtons() {
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

        mBinding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosition >0) mPosition--;
                if (mCurrentUserImages.get(mPosition) != null) {
                    Glide.with(mBinding.ivPhoto.getContext())
                            .load(Uri.parse(mCurrentUserImages.get(mPosition)))
                            .into(mBinding.ivPhoto);
                }
            }
        });

        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosition < mCurrentUserImages.size()-1) mPosition++;
                if (mCurrentUserImages.get(mPosition) != null) {
                    Glide.with(mBinding.ivPhoto.getContext())
                            .load(Uri.parse(mCurrentUserImages.get(mPosition)))
                            .into(mBinding.ivPhoto);
                }
            }
        });

        mBinding.fabInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser.getInstagram().equals("")) {
                    Toast.makeText(getContext(), "Please provide youR Instagram username!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Uri appUri = Uri.parse("https://instagram.com/_u/" + mCurrentUser.getInstagram());
                    Uri browserUri = Uri.parse("https://instagram.com/" + mCurrentUser.getInstagram());

                    try { //first try to open in instagram app
                        Intent appIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                        if(appIntent != null){
                            appIntent.setAction(Intent.ACTION_VIEW);
                            appIntent.setData(appUri);
                            startActivity(appIntent);
                        }
                    } catch(Exception e){ //or else open in browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                        startActivity(browserIntent);
                    }
                }
            }
        });
    }


    private void setVacationInfoListener() {
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Vacation object and use the values to update the UI
                mVacation = dataSnapshot.getValue(Vacation.class);
                populateVacationViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Vacation failed, log a message
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        mVacationDetailsReference.addValueEventListener(vacationListener);
    }


    private void setProfileInfoListener() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                mCurrentUser = dataSnapshot.getValue(User.class);
                populateProfileViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        mCurrentUserNodeReference.addValueEventListener(userListener);
    }


    private void populateProfileViews() {
        mBinding.tvNameAge.setText(mCurrentUser.getName() + ", " + mCurrentUser.getAge());
        mBinding.tvBio.setText(mCurrentUser.getBio());
        mBinding.tvFrom.setText(mCurrentUser.getFrom());
        mBinding.tvAdventureLevel.setText(mCurrentUser.getAdventureLevel() + "/5");
        populateImageList();
    }

    private void populateVacationViews() {
        if (!mVacation.getDestination().equals("")) {
            mBinding.tvLocationDate.setText(mVacation.getDestination() + "   |   " + mVacation.getStartDate() + " - " + mVacation.getEndDate());
        }
        else mBinding.tvLocationDate.setText("No vacation details yet");
        mBinding.tvVacationNotes.setText(mVacation.getNotes());

        if (mVacation.getAttraction1() != null) {
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

    private void displayAttraction(int i, String attraction) {
        mBullets.get(i).setVisibility(View.VISIBLE);
        mAttractionViews.get(i).setVisibility(View.VISIBLE);
        mAttractionViews.get(i).setText(attraction);
    }

    // fills ArrayList with Uri's for the user's images
    private void populateImageList() {
        mCurrentUserImages.clear();
        mCurrentUserImages.add(mCurrentUser.getImage1());
        mCurrentUserImages.add(mCurrentUser.getImage2());
        mCurrentUserImages.add(mCurrentUser.getImage3());
        populateImageView();
    }

    private void populateImageView() {
        if (mCurrentUser.getImage1() == null){
            mBinding.ivPhoto.setImageResource(R.drawable.add_image);
        } else {
            mPosition = 0;
            Glide.with(mBinding.ivPhoto.getContext())
                    .load(Uri.parse(mCurrentUserImages.get(mPosition)))
                    .into(mBinding.ivPhoto);
        }
    }

    private void goEditProfileFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current user", mCurrentUser);
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
}