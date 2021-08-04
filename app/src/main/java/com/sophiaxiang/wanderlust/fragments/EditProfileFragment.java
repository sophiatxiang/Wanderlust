package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.LoginActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentEditProfileBinding;
import com.sophiaxiang.wanderlust.models.User;

public class EditProfileFragment extends Fragment {
    public static final String TAG = "EditProfileFragment";
    private static final String[] GENDER_CHOICES = {"select a gender...", "female", "male", "other"};
    private FragmentEditProfileBinding mBinding;
    private DatabaseReference mDatabase;
    private DatabaseReference mCurrentUserNodeReference;
    private FirebaseUser mFirebaseUser;
    private User mCurrentUser;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        mCurrentUser = (User) bundle.getSerializable("current user");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUserNodeReference = mDatabase.child("users").child(mCurrentUser.getUserId());

        setUpToolBar(view);
        setUpButtons();

        populateEditTextViews();
        updateImageViews();
        populateImageViews();

        setUpGenderSpinner();
        setUpAdventureLevelSlider();
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    private void setUpButtons() {
        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser.getImage1() == null || mCurrentUser.getImage2() == null || mCurrentUser.getImage3() == null) {
                    Toast.makeText(getContext(), "Please provide 3 images!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDatabaseUserProfile();
            }
        });

        mBinding.ivImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("image1");
            }
        });

        mBinding.ivImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("image2");
            }
        });

        mBinding.ivImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("image3");
            }
        });

        mBinding.tvChngProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTakePhotoFragment("profilePhoto");
            }
        });
    }

    private void populateEditTextViews() {
        mBinding.etName.setText(mCurrentUser.getName());
        mBinding.etAge.setText("" + mCurrentUser.getAge());
        mBinding.etFrom.setText(mCurrentUser.getFrom());
        mBinding.etBio.setText(mCurrentUser.getBio());
        mBinding.etInstagram.setText("@" + mCurrentUser.getInstagram());
    }

    private void updateImageViews() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user object and use the values to update the UI
                mCurrentUser = dataSnapshot.getValue(User.class);
                populateImageViews();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting user failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        mCurrentUserNodeReference.addValueEventListener(userListener);
    }

    private void populateImageViews() {
        // populate user's images
        if (mCurrentUser.getImage1() == null) {
            mBinding.ivImage1.setImageResource(R.drawable.add_image);
        } else {
            Glide.with(mBinding.ivImage1.getContext())
                    .load(Uri.parse(mCurrentUser.getImage1()))
                    .placeholder(R.drawable.add_image)
                    .into(mBinding.ivImage1);
        }

        if (mCurrentUser.getImage2() == null) {
            mBinding.ivImage2.setImageResource(R.drawable.add_image);
        } else {
            Glide.with(mBinding.ivImage2.getContext())
                    .load(Uri.parse(mCurrentUser.getImage2()))
                    .placeholder(R.drawable.add_image)
                    .into(mBinding.ivImage2);
        }

        if (mCurrentUser.getImage3() == null) {
            mBinding.ivImage3.setImageResource(R.drawable.add_image);
        } else {
            Glide.with(mBinding.ivImage3.getContext())
                    .load(Uri.parse(mCurrentUser.getImage3()))
                    .placeholder(R.drawable.add_image)
                    .into(mBinding.ivImage3);
        }

        // populate profile pic
        if (mCurrentUser.getProfilePhoto() == null){
            Glide.with(mBinding.ivProfilePhoto.getContext())
                    .load(R.drawable.no_profile_pic)
                    .circleCrop()
                    .into(mBinding.ivProfilePhoto);
        } else {
            Glide.with(mBinding.ivProfilePhoto.getContext())
                    .load(Uri.parse(mCurrentUser.getProfilePhoto()))
                    .circleCrop()
                    .into(mBinding.ivProfilePhoto);
        }
    }

    private void updateDatabaseUserProfile() {
        if (TextUtils.isEmpty(mBinding.etAge.toString())){
            Toast.makeText(getContext(), "Please enter an age!", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = getEditedValues();
        mDatabase.child("users").child(mFirebaseUser.getUid()).setValue(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Save successful!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "failure to update profile: ", e);
                }
            });
    }

    private User getEditedValues() {
        String name = mBinding.etName.getText().toString();
        Integer age = Integer.parseInt(mBinding.etAge.getText().toString());
        String gender = mCurrentUser.getGender();
        String from = mBinding.etFrom.getText().toString();
        String bio = mBinding.etBio.getText().toString();
        int adventureLevel = mCurrentUser.getAdventureLevel();
        String image1 = mCurrentUser.getImage1();
        String image2 = mCurrentUser.getImage2();
        String image3 = mCurrentUser.getImage3();
        String profilePhoto = mCurrentUser.getProfilePhoto();
        String instagram = mBinding.etInstagram.getText().toString().substring(1);
        String songAlbumCover = mCurrentUser.getSongAlbumCover();
        String songName = mCurrentUser.getSongName();
        String songArtist = mCurrentUser.getSongArtist();
        String songPreviewUrl = mCurrentUser.getSongPreviewUrl();

        return new User(mFirebaseUser.getUid(), name , age, gender, from, bio,
                adventureLevel, image1, image2, image3, profilePhoto, mCurrentUser.getVacation(),
                instagram, songAlbumCover, songName, songArtist, songPreviewUrl);
    }

    private void goTakePhotoFragment(String imageNumber){
        Fragment fragment = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putString("current user id", mCurrentUser.getUserId());
        args.putString("image number", imageNumber);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setUpGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_spinner, GENDER_CHOICES) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.item_spinner);
        mBinding.spinnerGender.setAdapter(adapter);

        if (!mCurrentUser.getGender().equals("")){
            int spinnerPosition = adapter.getPosition(mCurrentUser.getGender());
            mBinding.spinnerGender.setSelection(spinnerPosition);
        }
        mBinding.spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText;
                switch (position) {
                    case 0:
                        mCurrentUser.setGender("");
                        break;
                    case 1:
                    case 2:
                    case 3:
                        selectedItemText = (String) parent.getItemAtPosition(position);
                        mCurrentUser.setGender(selectedItemText);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCurrentUser.setGender("");
            }
        });
    }

    private void setUpAdventureLevelSlider() {
        mBinding.sliderAdventureLevel.setValue(mCurrentUser.getAdventureLevel());
        mBinding.sliderAdventureLevel.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mCurrentUser.setAdventureLevel((int) value);
            }
        });
    }
}