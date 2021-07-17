package com.sophiaxiang.wanderlust.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentChatBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Chat> chats;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            tvChatName = itemView.findViewById(R.id.tvChatName);
        }

        public void bind(Chat chat) {
            //TODO: change image1 to profile picture
            Glide.with(context).load(chat.getOtherUser().getImage1()).into(ivChatImage);
            tvChatName.setText(chat.getOtherUser().getName());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // TODO: go to selected individual chat
            }
        }
    }
}
