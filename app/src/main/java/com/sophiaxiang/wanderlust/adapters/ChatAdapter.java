package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sophiaxiang.wanderlust.fragments.UserDetailsFragment;
import com.sophiaxiang.wanderlust.models.Chat;
import com.sophiaxiang.wanderlust.models.User;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    public static final String TAG = "ChatAdapter";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final List<Chat> mChats;
    private final Context mContext;

    public ChatAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.mChats = chats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView ivChatImage;
        private final TextView tvChatName;
        private final TextView tvLastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            tvChatName = itemView.findViewById(R.id.tvChatName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);

            itemView.setOnClickListener(this);

            ivChatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goUserDetailsFrag();
                }
            });
        }

        // get selected user's details from Realtime Database and launch User Details fragment
        private void goUserDetailsFrag() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mDatabase.child("users").child(mChats.get(position).getOtherUserId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);
                        AppCompatActivity activity = (AppCompatActivity) mContext;
                        Fragment fragment = new UserDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        fragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        }

        public void bind(Chat chat) {
            // if profile pic is available, load into image view
            DatabaseReference mUserProfilePicReference = mDatabase.child("users").child(chat.getOtherUserId()).child("profilePhoto");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(String.class);
                    if (uri != null) {
                        Glide.with(mContext)
                                .load(Uri.parse(uri))
                                .circleCrop()
                                .into(ivChatImage);
                    } else {
                        Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop().into(ivChatImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "load user image:onCancelled", databaseError.toException());
                }
            };
            mUserProfilePicReference.addValueEventListener(listener);

            tvChatName.setText(chat.getOtherUserName());
            tvLastMessage.setText(chat.getLastMessage());
        }

        @Override
        public void onClick(View v) {
            // launch Chat Details Activity
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(mContext, ChatDetailsActivity.class);
                intent.putExtra("current user id", mChats.get(position).getCurrentUserId());
                intent.putExtra("other user id", mChats.get(position).getOtherUserId());
                intent.putExtra("other user name", mChats.get(position).getOtherUserName());
                mContext.startActivity(intent);
            }
        }
    }
}
