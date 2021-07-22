package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.fragments.EditProfileFragment;
import com.sophiaxiang.wanderlust.fragments.UserDetailsFragment;
import com.sophiaxiang.wanderlust.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<User> mUsers;
    Context mContext;

    public UserAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.mUsers = users;
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

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivUserPhoto;
        TextView tvUserNameAge;
        TextView tvUserDestination;
        TextView tvUserVacationDate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvUserNameAge = itemView.findViewById(R.id.tvUserNameAge);
            tvUserDestination = itemView.findViewById(R.id.tvUserDestination);
            tvUserVacationDate = itemView.findViewById(R.id.tvUserVacationDate);
            itemView.setOnClickListener(this);
        }

        public void bind(User user) {
            Glide.with(mContext).load(Uri.parse(user.getImage1())).into(ivUserPhoto);
            tvUserNameAge.setText(user.getName() + ", " + user.getAge());
            tvUserDestination.setText(user.getVacation().getDestination());
            tvUserVacationDate.setText(user.getVacation().getStartDate() + " - " + user.getVacation().getEndDate());
        }

        @Override
        public void onClick(View v) {
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
        }
    }
}
