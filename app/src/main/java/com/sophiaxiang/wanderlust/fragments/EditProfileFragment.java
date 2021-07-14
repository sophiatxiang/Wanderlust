package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.LoginActivity;
import com.sophiaxiang.wanderlust.MainActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentEditProfileBinding;
import com.sophiaxiang.wanderlust.models.User;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private DatabaseReference mDatabase;
    FirebaseUser user;

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

    private void updateDatabaseUserProfile() {
        String name = binding.etName.getText().toString();
        Integer age = Integer.parseInt(binding.etAge.getText().toString());
        String pronouns = binding.etPronouns.getText().toString();
        String from = binding.etFrom.getText().toString();
        String bio = binding.etBio.getText().toString();
        User test = new User(user.getUid(), name , age, pronouns, from, bio);
        mDatabase.child("users").child(user.getUid()).setValue(test);
    }

    public void goLoginActivity () {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}