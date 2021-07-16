package com.sophiaxiang.wanderlust.fragments;

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
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserNodeReference;
    private DatabaseReference vacationDetailsReference;
    private StorageReference storageReference;
    private String currentUserId;
    private User user;
    private Vacation vacation;

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
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUserNodeReference = mDatabase.child("users").child(currentUserId);
        vacationDetailsReference = mDatabase.child("vacations").child(currentUserId);

        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("current user");
        vacation = (Vacation) bundle.getSerializable("vacation");

        populateViews();
        setProfileInfoListener();
        setVacationInfoListener();

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
    }

    private void setVacationInfoListener() {
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                vacation = dataSnapshot.getValue(Vacation.class);
                // TODO: populate vacation views
//                populateVacationViews();
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
                populateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserNodeReference.addValueEventListener(userListener);
    }

    private void populateViews() {
        binding.tvNameAge.setText(user.getName() + ", " + user.getAge());
        binding.tvBio.setText(user.getBio());
        binding.tvFrom.setText(user.getFrom());
        binding.tvAdventureLevel.setText(user.getAdventureLevel());
        binding.tvLocationDate.setText(vacation.getDestination() + "   |   " + vacation.getStartDate() + " - " + vacation.getEndDate());
        binding.tvVacationNotes.setText(vacation.getNotes());
        populateImageView();
    }

    private void populateImageView() {
        if (user.getImage1() == null && user.getImage2() == null && user.getImage3() == null) {
            binding.ivPhotos.setImageResource(R.drawable.add_image);
        }
        else if (user.getImage1() != null) {
            Glide.with(binding.ivPhotos.getContext())
                    .load(Uri.parse(user.getImage1()))
                    .centerCrop()
                    .into(binding.ivPhotos);
        }
        else if (user.getImage2() != null) {
            Glide.with(binding.ivPhotos.getContext())
                    .load(Uri.parse(user.getImage2()))
                    .centerCrop()
                    .into(binding.ivPhotos);
        }
        else {
            Glide.with(binding.ivPhotos.getContext())
                    .load(Uri.parse(user.getImage3()))
                    .centerCrop()
                    .into(binding.ivPhotos);
        }
    }

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