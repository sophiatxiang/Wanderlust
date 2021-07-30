package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;
    private static final String TAG = "ChatMessageAdapter";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private List<ChatMessage> mMessages;
    private Context mContext;
    private String mCurrentUserId;

    public ChatMessageAdapter(Context context, String userId, List<ChatMessage> messages) {
        mMessages = messages;
        this.mCurrentUserId = userId;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.MessageViewHolder holder, int position) {
        ChatMessage message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (checkSenderIsCurrentUser(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean checkSenderIsCurrentUser(int position) {
        ChatMessage message = mMessages.get(position);
        return message.getMessageSender() != null && message.getMessageSender().equals(mCurrentUserId);
    }


    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(ChatMessage message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView ivProfileOther;
        TextView tvBody;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            ivProfileOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(ChatMessage message) {
            DatabaseReference mUserProfilePicReference = mDatabase.child("users").child(message.getMessageSender()).child("profilePhoto");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // if profile pic is available, load into imageview
                    String uri = dataSnapshot.getValue(String.class);
                    if (uri != null) {
                        Glide.with(mContext)
                                .load(Uri.parse(uri))
                                .circleCrop()
                                .into(ivProfileOther);
                    }
                    else Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop().into(ivProfileOther);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "load incoming message profile image:onCancelled", databaseError.toException());
                }
            };
            mUserProfilePicReference.addValueEventListener(listener);

            tvBody.setText(message.getMessageText());
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView ivProfileMe;
        TextView tvBody;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            ivProfileMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(ChatMessage message) {
            // if profile pic is available, load into image view
            DatabaseReference mUserProfilePicReference = mDatabase.child("users").child(message.getMessageSender()).child("profilePhoto");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(String.class);
                    if (uri != null) {
                        Glide.with(mContext)
                                .load(Uri.parse(uri))
                                .circleCrop()
                                .into(ivProfileMe);
                    }
                    else Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop().into(ivProfileMe);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "load outgoing message profile image:onCancelled", databaseError.toException());
                }
            };
            mUserProfilePicReference.addValueEventListener(listener);

            tvBody.setText(message.getMessageText());
        }
    }
}

