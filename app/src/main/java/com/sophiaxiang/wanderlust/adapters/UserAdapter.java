package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<User> users;
    Context context;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
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
            Glide.with(context).load(Uri.parse(user.getImage1())).into(ivUserPhoto);
            tvUserNameAge.setText(user.getName() + ", " + user.getAge());
            tvUserDestination.setText(user.getVacation().getDestination());
            tvUserVacationDate.setText(user.getVacation().getStartDate() + " - " + user.getVacation().getEndDate());
        }

        @Override
        public void onClick(View v) {
            //TODO: launch profile details fragment
            Toast.makeText(context, "view holder clicked", Toast.LENGTH_SHORT).show();



        }
    }
}
