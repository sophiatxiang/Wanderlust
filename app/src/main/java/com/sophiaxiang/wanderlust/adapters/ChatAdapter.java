package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.ChatDetailsActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentChatBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final String TAG = "ChatAdapter";
    private List<Chat> chats;
    private Context context;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivChatImage;
        private TextView tvChatName;
        private TextView tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            tvChatName = itemView.findViewById(R.id.tvChatName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            itemView.setOnClickListener(this);
        }

        public void bind(Chat chat) {
            //TODO: change image1 to profile picture
//            DatabaseReference mUserProfilePicReference = mDatabase.child("users").child(chat.getOtherUserId()).child("profileImage");
//            ValueEventListener postListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    String uri = dataSnapshot.getValue(String.class);
//
//                    Glide.with(context).load(Uri.parse(uri)).into(ivChatImage);
//                    tvChatName.setText(chat.getOtherUserName());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Getting Post failed, log a message
//                    Log.w(TAG, "load user image:onCancelled", databaseError.toException());
//                }
//            };
//            mUserProfilePicReference.addValueEventListener(postListener);

            Glide.with(context).load(R.drawable.no_profile_pic).circleCrop().into(ivChatImage);

            tvChatName.setText(chat.getOtherUserName());
            tvLastMessage.setText(chat.getLastMessage());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("current user id", chats.get(position).getCurrentUserId());
                intent.putExtra("other user id", chats.get(position).getOtherUserId());
                context.startActivity(intent);
            }
        }
    }
}
