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

        updateProfileInfo();
        updateVacationInfo();

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
//
//    private void populateImageViews() {
//        getDatabaseImageUris();
//    }
//
//    private void loadImagesIntoImageViews() {
//        Glide.with(binding.ivPhotos.getContext())
//                .load(Uri.parse(user.getImageUriList().get(0)))
//                .centerCrop()
//                .into(binding.ivPhotos);
//    }
//
//    private void getDatabaseImageUris() {
//        StorageReference imageReference1 = storageReference.child(currentUserId).child("image1.jpg");
//        imageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                if (uri != null) {
//                    user.getImageUriList().add(0, uri.toString());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e(TAG, "failed to get image 1: ", exception);
//            }
//        });
//
//        StorageReference imageReference2 = storageReference.child(currentUserId).child("image2.jpg");
//        imageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                if (uri != null) {
//                    user.getImageUriList().add(uri.toString());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e(TAG, "failed to get image 2: ", exception);
//            }
//        });
//
//        StorageReference imageReference3 = storageReference.child(currentUserId).child("image3.jpg");
//        imageReference3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                if (uri != null) {
//                    user.getImageUriList().add(uri.toString());
//                    loadImagesIntoImageViews();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e(TAG, "failed to get image 3: ", exception);
//                if (user.getImageUriList().size() == 0) binding.ivPhotos.setImageResource(R.drawable.add_image);
//            }
//        });
//    }

    private void updateVacationInfo() {
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


    private void updateProfileInfo() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                user.setImageUriList(new ArrayList<>());
                populateViews();
//                populateImageViews();
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