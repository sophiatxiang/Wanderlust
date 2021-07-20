package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.adapters.ChatMessageAdapter;
import com.sophiaxiang.wanderlust.databinding.ActivityChatDetailsBinding;
import com.sophiaxiang.wanderlust.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity {
    public static final String TAG = "ChatDetailsActivity";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    private ActivityChatDetailsBinding binding;
    private String currentUserId;
    private String otherUserId;
    private String chatId;
    private DatabaseReference mDatabase;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getChatId();
        setUpMessagePosting();
        setUpNewMessageListener();
    }

    private void setUpNewMessageListener() {
        Query recentMessagesQuery = mDatabase.child("chats").child(chatId).limitToFirst(40);
        recentMessagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                mMessages.add(0, message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemInserted(0);
                        binding.rvChat.scrollToPosition(0);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getChatId() {
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("current user id");
        otherUserId = intent.getStringExtra("other user id");
        if (currentUserId.compareTo(otherUserId) < 0)
            chatId = currentUserId + otherUserId;
        else chatId =  otherUserId + currentUserId;
    }

    // Set up button event handler which posts the entered message to Parse
    void setUpMessagePosting() {
        mMessages = new ArrayList<>();
        mAdapter = new ChatMessageAdapter(ChatDetailsActivity.this, currentUserId, mMessages);
        binding.rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        binding.rvChat.setLayoutManager(linearLayoutManager);

        setOnClick();
    }

    private void setOnClick() {
        // When send button is clicked, create message object on Parse
        binding.ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create message in individual chat history
                String data = binding.etMessage.getText().toString();
                ChatMessage message = new ChatMessage(currentUserId, data);
                mDatabase.child("chats").child(chatId).push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Successfully created message in Firebase",
                                Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    });

                // update last message in current user's chat lists
                mDatabase.child("userChatLists").child(currentUserId).child(chatId).child("lastMessage").setValue(message.getMessageText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Successfully updated lastMessage",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to save message", e);
                            }
                        });

                // update last message time in current user's chat lists
                mDatabase.child("userChatLists").child(currentUserId).child(chatId).child("lastMessageTime").setValue(message.getMessageTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Successfully updated lastMessageTime",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to save message", e);
                            }
                        });

                // update last message in other user's chat lists
                mDatabase.child("userChatLists").child(otherUserId).child(chatId).child("lastMessage").setValue(message.getMessageText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Successfully updated lastMessage for other user",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to save message", e);
                            }
                        });

                // update last message time in other user's chat lists
                mDatabase.child("userChatLists").child(otherUserId).child(chatId).child("lastMessageTime").setValue(message.getMessageTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Successfully updated lastMessageTime for other user",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to save message", e);
                            }
                        });

                binding.etMessage.setText(null);
            }
        });
    }
}