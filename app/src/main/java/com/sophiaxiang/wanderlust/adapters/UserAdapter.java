package com.sophiaxiang.wanderlust.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.fragments.UserDetailsFragment;
import com.sophiaxiang.wanderlust.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final List<User> mUsers;
    private final Context mContext;
    private final String mCurrentUserId;

    public UserAdapter(Context context, List<User> users, String currentUserId) {
        this.mContext = context;
        this.mUsers = users;
        this.mCurrentUserId = currentUserId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPhoto;
        TextView tvUserNameAge;
        TextView tvUserDestination;
        TextView tvUserVacationDate;
        ImageView ivHeart;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvUserNameAge = itemView.findViewById(R.id.tvUserNameAge);
            tvUserDestination = itemView.findViewById(R.id.tvUserDestination);
            tvUserVacationDate = itemView.findViewById(R.id.tvUserVacationDate);
            ivHeart = itemView.findViewById(R.id.ivHeart);

            setUpOnTouchListener(itemView);
        }


        public void bind(User user) {
            if (user.getImage1() != null) {
                Glide.with(mContext).load(Uri.parse(user.getImage1())).into(ivUserPhoto);
            } else {
                Glide.with(mContext).load(R.drawable.add_image).into(ivUserPhoto);
            }
            tvUserNameAge.setText(user.getName() + ", " + user.getAge());
            tvUserDestination.setText(user.getVacation().getDestination());
            tvUserVacationDate.setText(user.getVacation().getStartDate() + " - " + user.getVacation().getEndDate());
        }

        private void setUpOnTouchListener(View itemView) {
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private final GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        int position = getAdapterPosition();
                        User thisUser = mUsers.get(position);
                        mDatabase.child("likedUserLists").child(mCurrentUserId).child(thisUser.getUserId()).child("likedAt").setValue(System.currentTimeMillis());

                        Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
                        ivHeart.startAnimation(fadeIn);
                        ivHeart.setVisibility(View.VISIBLE);

                        ivHeart.postDelayed(new Runnable() {
                            public void run() {
                                Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fadeout);
                                ivHeart.startAnimation(fadeOut);
                                ivHeart.setVisibility(View.GONE);
                            }
                        }, 1000);
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            AppCompatActivity activity = (AppCompatActivity) mContext;
                            Fragment fragment = new UserDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", mUsers.get(position));
                            fragment.setArguments(bundle);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.flContainer, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                        return super.onSingleTapConfirmed(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
    }
}
