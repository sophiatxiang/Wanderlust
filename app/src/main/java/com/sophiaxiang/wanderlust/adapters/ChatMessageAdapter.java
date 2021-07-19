package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.models.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    private List<ChatMessage> mMessages;
    private Context mContext;
    private String mUserId;

    public ChatMessageAdapter(Context context, String userId, List<ChatMessage> messages) {
        mMessages = messages;
        this.mUserId = userId;
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
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        ChatMessage message = mMessages.get(position);
        return message.getMessageSender() != null && message.getMessageSender().equals(mUserId);
    }


    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(ChatMessage message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            body = (TextView) itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(ChatMessage message) {
            //TODO: ADD PROFILE PICTURE
            Glide.with(mContext)
                    .load(R.drawable.no_profile_pic)
                    .circleCrop() // create an effect of a round profile picture
                    .into(imageOther);
            body.setText(message.getMessageText());
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            body = (TextView) itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(ChatMessage message) {
            // TODO: ADD PROFILE PICTURE
            Glide.with(mContext)
                    .load(R.drawable.no_profile_pic)
                    .circleCrop() // create an effect of a round profile picture
                    .into(imageMe);
            body.setText(message.getMessageText());
        }
    }
}

