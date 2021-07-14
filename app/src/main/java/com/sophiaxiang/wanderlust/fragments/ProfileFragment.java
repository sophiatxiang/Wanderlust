package com.sophiaxiang.wanderlust.fragments;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.models.User;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private DatabaseReference mDatabase;
    private DatabaseReference currentUserNodeReference;
    private String currentUserId;
    private User user;

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

        updateProfileInfo();

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditProfileFrag();
            }
        });
    }

    private void updateProfileInfo() {
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        currentUserNodeReference.addValueEventListener(userListener);
    }

    private void populateViews() {
        binding.tvNameAge.setText(user.getName());
        binding.tvBio.setText(user.getBio());
        binding.tvFrom.setText(user.getFrom());
        binding.tvAdventureLevel.setText(user.getAdventureLevel());
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
}