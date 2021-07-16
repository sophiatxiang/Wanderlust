package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.List;

public class EditProfileFragment extends Fragment {

    public static final String TAG = "EditProfileFragment";
    private FragmentEditProfileBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private User currentUser;
    private StorageReference storageReference;


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
        storageReference = FirebaseStorage.getInstance().getReference();

        populateEditTextViews();
        populateImageViews();

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

       binding.ivImage1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               goTakePhotoFragment("image1");
           }
       });

        binding.ivImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("image2");
            }
        });

        binding.ivImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("image3");
            }
        });
    }

    private void populateImageViews() {
        if (currentUser.getImage1() == null) {
            binding.ivImage1.setImageResource(R.drawable.add_image);
        }
        else {
            Glide.with(binding.ivImage1.getContext())
                    .load(Uri.parse(currentUser.getImage1()))
                    .placeholder(R.drawable.add_image)
                    .into(binding.ivImage1);
        }
        if (currentUser.getImage2() == null) {
            binding.ivImage2.setImageResource(R.drawable.add_image);
        }
        else {
            Glide.with(binding.ivImage2.getContext())
                    .load(Uri.parse(currentUser.getImage2()))
                    .placeholder(R.drawable.add_image)
                    .into(binding.ivImage2);
        }
        if (currentUser.getImage3() == null) {
            binding.ivImage3.setImageResource(R.drawable.add_image);
        }
        else {
            Glide.with(binding.ivImage3.getContext())
                    .load(Uri.parse(currentUser.getImage3()))
                    .placeholder(R.drawable.add_image)
                    .into(binding.ivImage3);
        }
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

    private void goTakePhotoFragment(String imageNumber){
        Fragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putString("current user id", currentUser.getUserId());
        args.putString("image number", imageNumber);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void goLoginActivity () {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}