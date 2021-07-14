package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sophiaxiang.wanderlust.LoginActivity;
import com.sophiaxiang.wanderlust.MainActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentEditProfileBinding;
import com.sophiaxiang.wanderlust.models.User;

public class EditProfileFragment extends Fragment {

    public static final String TAG = "EditProfileFragment";
    private FragmentEditProfileBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private User currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference currentUserStorageRef;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        currentUser = (User) bundle.getSerializable("current user");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUserStorageRef = storageRef.child(currentUser.getUserId());

        populateEditTextViews();

       binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                goLoginActivity();
            }
        });

       binding.btnSave.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               updateDatabaseUserProfile();
               getActivity().getSupportFragmentManager().popBackStack();
           }
       });
    }

    private void populateEditTextViews() {
        binding.etName.setText(currentUser.getName());
        binding.etAge.setText("" + currentUser.getAge());
        binding.etGender.setText(currentUser.getGender());
        binding.etFrom.setText(currentUser.getFrom());
        binding.etBio.setText(currentUser.getBio());
        binding.etAdventureLevel.setText(currentUser.getAdventureLevel());
    }

    private void updateDatabaseUserProfile() {
        if (TextUtils.isEmpty(binding.etAge.toString())){
            Toast.makeText(getContext(), "Please enter an age!", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = binding.etName.getText().toString();
        Integer age = Integer.parseInt(binding.etAge.getText().toString());
        String gender = binding.etGender.getText().toString();
        String from = binding.etFrom.getText().toString();
        String bio = binding.etBio.getText().toString();
        String adventureLevel = binding.etAdventureLevel.getText().toString();
        User user = new User(firebaseUser.getUid(), name , age, gender, from, bio, adventureLevel);
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
    }

    public void goLoginActivity () {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}