package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;

public class UserProfileFragment extends AbstractProfileFragment {
    private String mCurrentUserName;
    private String mChatId;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentUserName();
        checkIfUserLiked();
        checkIfHasInstagram();
    }

    private void getCurrentUserName() {
        mDatabase.child("users").child(mCurrentUserId).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    mCurrentUserName = String.valueOf(task.getResult().getValue());
                }
            }
        });
    }

    private void checkIfUserLiked() {
        mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mBinding.fabLike.setSelected(snapshot.exists());
                mBinding.fabLikeUserDetails.setSelected(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled checkIfUserLiked");
            }
        });
    }

    private void checkIfHasInstagram() {
        if (mUser.getInstagram().equals("")) {
            // hide Instagram fab and show the Like fab in its place
            mBinding.fabInstagram.setVisibility(View.GONE);
            mBinding.fabLike.setVisibility(View.GONE);
            mBinding.fabLikeUserDetails.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setUpButtons() {
        super.setUpButtons();
        mBinding.btnChat.setVisibility(View.VISIBLE);
        mBinding.fabLike.setVisibility(View.VISIBLE);
        mBinding.btnEditProfile.setVisibility(View.GONE);
        mBinding.btnEditVacation.setVisibility(View.GONE);

        mBinding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpNewChat();
                goChatDetails();
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

        mBinding.fabLikeUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.fabLikeUserDetails.isSelected()) {
                    mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).child("likedAt").setValue(System.currentTimeMillis());
                    mBinding.fabLikeUserDetails.setSelected(true);
                } else {
                    mDatabase.child("likedUserLists").child(mCurrentUserId).child(mUser.getUserId()).removeValue();
                    mBinding.fabLikeUserDetails.setSelected(false);
                }
            }
        });
    }

    protected void populateMusicViews() {
        if (mUser.getSongName() == null) {
            mBinding.rlMusic.setVisibility(View.GONE);
            return;
        }
        mBinding.tvSongName.setText(mUser.getSongName());
        mBinding.tvSongArtist.setText(mUser.getSongArtist());
        Glide.with(getContext()).load(mUser.getSongAlbumCover()).placeholder(R.drawable.album_placeholder).into(mBinding.ivAlbumCover);
    }

    private void setUpNewChat() {
        if (mCurrentUserId.compareTo(mUser.getUserId()) < 0) {
            mChatId = mCurrentUserId + mUser.getUserId();
        } else {
            mChatId = mUser.getUserId() + mCurrentUserId;
        }
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