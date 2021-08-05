package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.fragments.UserProfileFragment;
import com.sophiaxiang.wanderlust.models.User;

import java.util.List;

public class LikedUserAdapter extends RecyclerView.Adapter<LikedUserAdapter.LikedUserViewHolder> {
    public static final String TAG = "LikedUserAdapter";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final Context mContext;
    private final List<String> mLikedUserIds;
    private final String mCurrentUserId;
    private String mOtherUserName;

    public LikedUserAdapter(Context mContext, List<String> mLikedUserIds, String mCurrentUserId) {
        this.mContext = mContext;
        this.mLikedUserIds = mLikedUserIds;
        this.mCurrentUserId = mCurrentUserId;
    }

    @NonNull
    @Override
    public LikedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_liked_user, parent, false);
        return new LikedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedUserAdapter.LikedUserViewHolder holder, int position) {
        String otherUserId = mLikedUserIds.get(position);
        holder.bind(otherUserId);
    }

    @Override
    public int getItemCount() {
        return mLikedUserIds.size();
    }

    public class LikedUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivLikedUserImage;
        TextView tvLikedUserName;
        Button btnChatInLikesFrag;

        public LikedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLikedUserImage = itemView.findViewById(R.id.ivLikedUserImage);
            tvLikedUserName = itemView.findViewById(R.id.tvLikedUserName);
            btnChatInLikesFrag = itemView.findViewById(R.id.btnChatInLikesFrag);

            itemView.setOnClickListener(this);

            btnChatInLikesFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mDatabase.child("users").child(mLikedUserIds.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                User user = task.getResult().getValue(User.class);
                                goChatDetailsFrag(user);
                            }
                        });
                    }
                }
            });
        }

        public void bind(String otherUserId) {
            populateUserImage(otherUserId);
            populateUserName(otherUserId);
        }

        @Override
        public void onClick(View v) {
            // get user at clicked position and launch user details fragment
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mDatabase.child("users").child(mLikedUserIds.get(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);
                        goUserDetailsFrag(user);
                    }
                });
            }
        }

        private void goChatDetailsFrag(User user) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(mContext, ChatDetailsActivity.class);
                intent.putExtra("current user id", mCurrentUserId);
                intent.putExtra("other user id", user.getUserId());
                intent.putExtra("other user name", user.getName());
                mContext.startActivity(intent);
            }
        }

        private void goUserDetailsFrag(User user) {
            AppCompatActivity activity = (AppCompatActivity) mContext;
            Fragment fragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        private void populateUserImage(String otherUserId) {
            DatabaseReference mUserProfilePicReference = mDatabase.child("users").child(otherUserId).child("profilePhoto");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(String.class);
                    if (uri != null) {
                        Glide.with(mContext)
                                .load(Uri.parse(uri))
                                .circleCrop()
                                .into(ivLikedUserImage);
                    } else {
                        Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop().into(ivLikedUserImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "load user image:onCancelled", databaseError.toException());
                }
            };
            mUserProfilePicReference.addValueEventListener(listener);
        }

        private void populateUserName(String otherUserId) {
            mDatabase.child("users").child(otherUserId).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    mOtherUserName = (String) task.getResult().getValue();
                    tvLikedUserName.setText(mOtherUserName);
                }
            });
        }
    }
}
