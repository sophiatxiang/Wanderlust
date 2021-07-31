package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentUserDetailsBinding;
import com.sophiaxiang.wanderlust.models.Chat;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDetailsFragment extends Fragment {

    public static final String TAG = "UserDetailsFragment";
    private FragmentUserDetailsBinding mBinding;
    private DatabaseReference mDatabase;
    private DatabaseReference mCurrentUserNodeReference;
    private DatabaseReference mVacationDetailsReference;
    private User mUser;
    private Vacation mVacation;
    private List<String> mUserImages;
    private String mCurrentUserId;
    private String mCurrentUserName;
    private int mPosition = 0;
    private String mChatId;
    private List<TextView> mAttractionViews;
    private List<ImageView> mBullets;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_details, container, false);
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
        populateImageList();

        setUpButtons();
        getCurrentUserName();
        checkIfUserLiked();
        checkIfHasInstagram();

        populateProfileViews();
        populateVacationViews();

        setProfileInfoListener();
        setVacationInfoListener();
    }

    private void getCurrentUserName() {
        mDatabase.child("users").child(mCurrentUserId).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                   mCurrentUserName = String.valueOf(task.getResult().getValue());
                }
            }
        });
    }

    private void checkIfUserLiked() {
       mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()) {
                   mBinding.fabLike.setSelected(true);
               } else {
                   mBinding.fabLike.setSelected(false);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Log.d(TAG, "onCancelled checkIfUserLiked");
           }
       });
    }

    private void checkIfHasInstagram() {
        if (mUser.getInstagram().equals("")) {
            mBinding.fabInstagram.setVisibility(View.GONE);
        }
    }

    private void setUpButtons() {
        mBinding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpNewChat();
                goChatDetails();
            }
        });

        mBinding.btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosition >0) {
                    mPosition--;
                }
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
                if (mPosition < mUserImages.size() - 1) {
                    mPosition++;
                }
                if (mUserImages.get(mPosition) != null) {
                    Glide.with(mBinding.ivPhoto.getContext())
                            .load(Uri.parse(mUserImages.get(mPosition)))
                            .into(mBinding.ivPhoto);
                }
            }
        });

        mBinding.fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.fabLike.isSelected()) {
                    mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).child("likedAt").setValue(System.currentTimeMillis());
                    mBinding.fabLike.setSelected(true);
                } else {
                    mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).removeValue();
                    mBinding.fabLike.setSelected(false);
                }
            }
        });

        mBinding.fabInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri appUri = Uri.parse("https://instagram.com/_u/" + mUser.getInstagram());
                Uri browserUri = Uri.parse("https://instagram.com/" + mUser.getInstagram());

                try{ //first try to open in instagram app
                    Intent appIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                    if(appIntent != null){
                        appIntent.setAction(Intent.ACTION_VIEW);
                        appIntent.setData(appUri);
                        startActivity(appIntent);
                    }
                }catch(Exception e){ //or else open in browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                    startActivity(browserIntent);
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

    private void populateProfileViews() {
        mBinding.tvNameAge.setText(mUser.getName() + ", " + mUser.getAge());
        mBinding.tvBio.setText(mUser.getBio());
        mBinding.tvFrom.setText(mUser.getFrom());
        mBinding.tvAdventureLevel.setText(mUser.getAdventureLevel() + "/5");
        populateImageList();
    }

    private void populateVacationViews() {
        if (!mVacation.getDestination().equals("")) {
            mBinding.tvLocationDate.setText(mVacation.getDestination() + "   |   " + mVacation.getStartDate() + " - " + mVacation.getEndDate());
        }
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
        mUserImages.clear();
        mUserImages.add(mUser.getImage1());
        mUserImages.add(mUser.getImage2());
        mUserImages.add(mUser.getImage3());
        populateImageView();
    }

    private void populateImageView() {
        if (mUser.getImage1() == null) {
            mBinding.ivPhoto.setImageResource(R.drawable.add_image);
        } else {
            mPosition = 0;
            Glide.with(mBinding.ivPhoto.getContext())
                    .load(Uri.parse(mUserImages.get(mPosition)))
                    .into(mBinding.ivPhoto);
        }
    }

    private void setUpNewChat() {
        if (mCurrentUserId.compareTo(mUser.getUserId()) < 0)
            mChatId = mCurrentUserId + mUser.getUserId();
        else mChatId =  mUser.getUserId() + mCurrentUserId;

        mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("chatId").setValue(mChatId);
        mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("currentUserId").setValue(mCurrentUserId);
        mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("otherUserId").setValue(mUser.getUserId());
        mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("otherUserName").setValue(mUser.getName());
        mDatabase.child("userChatLists").child(mUser.getUserId()).child(mChatId).child("chatId").setValue(mChatId);
        mDatabase.child("userChatLists").child(mUser.getUserId()).child(mChatId).child("currentUserId").setValue(mUser.getUserId());
        mDatabase.child("userChatLists").child(mUser.getUserId()).child(mChatId).child("otherUserId").setValue(mCurrentUserId);
        mDatabase.child("userChatLists").child(mUser.getUserId()).child(mChatId).child("otherUserName").setValue(mCurrentUserName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                goChatDetails();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "setUpNewChat onFailure", e);
            }
        });
    }

    private void goChatDetails() {
            Intent intent = new Intent(getContext(), ChatDetailsActivity.class);
            intent.putExtra("current user id", mCurrentUserId);
            intent.putExtra("other user id", mUser.getUserId());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getActivity().startActivity(intent);
    }
}