package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.MainActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserNodeReference;
    private DatabaseReference vacationDetailsReference;
    private String currentUserId;
    private User user;
    private Vacation vacation;
    private List<String> currentUserImages;
    private int position = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserNodeReference = mDatabase.child("users").child(currentUserId);
        vacationDetailsReference = mDatabase.child("users").child(currentUserId).child("vacation");

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("current user");
        vacation = (Vacation) bundle.getSerializable("vacation");

        currentUserImages = new ArrayList<>();
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
    }

    private void setUpButtons() {
        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditProfileFrag();
            }
        });

        binding.btnEditVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditVacationFrag();
            }
        });
        binding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position>0)
                    position--;
                Glide.with(binding.ivPhoto.getContext())
                        .load(Uri.parse(currentUserImages.get(position)))
                        .into(binding.ivPhoto);
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<currentUserImages.size()-1)
                    position++;
                Glide.with(binding.ivPhoto.getContext())
                        .load(Uri.parse(currentUserImages.get(position)))
                        .into(binding.ivPhoto);
            }
        });
    }


    private void setVacationInfoListener() {
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                vacation = dataSnapshot.getValue(Vacation.class);
                populateVacationViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        vacationDetailsReference.addValueEventListener(vacationListener);
    }


    private void setProfileInfoListener() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                populateProfileViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserNodeReference.addValueEventListener(userListener);
    }

    private void populateProfileViews() {
        binding.tvNameAge.setText(user.getName() + ", " + user.getAge());
        binding.tvBio.setText(user.getBio());
        binding.tvFrom.setText(user.getFrom());
        binding.tvAdventureLevel.setText(user.getAdventureLevel() + "/5");
        populateImageList();
    }


    private void populateVacationViews() {
        if (!vacation.getDestination().equals("")) {
            binding.tvLocationDate.setText(vacation.getDestination() + "   |   " + vacation.getStartDate() + " - " + vacation.getEndDate());
        }
        else binding.tvLocationDate.setText("No vacation details yet");
        binding.tvVacationNotes.setText(vacation.getNotes());
    }


    private void populateImageList() {
        currentUserImages.clear();
        currentUserImages.add(user.getImage1());
        currentUserImages.add(user.getImage2());
        currentUserImages.add(user.getImage3());
        populateImageView();
    }


    private void populateImageView() {
        if (user.getImage1() == null){
            binding.ivPhoto.setImageResource(R.drawable.add_image);
        }
        else {
            position = 0;
            Glide.with(binding.ivPhoto.getContext())
                    .load(Uri.parse(currentUserImages.get(position)))
                    .into(binding.ivPhoto);
        }
    }

    // launch edit Profile Fragment
    private void goEditProfileFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current user", user);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    // launch Edit Vacation Fragment
    private void goEditVacationFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new MyVacationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("vacation", vacation);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}