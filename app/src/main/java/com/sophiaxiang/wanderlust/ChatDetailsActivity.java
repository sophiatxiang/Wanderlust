package com.sophiaxiang.wanderlust;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sophiaxiang.wanderlust.adapters.ChatMessageAdapter;
import com.sophiaxiang.wanderlust.databinding.ActivityChatDetailsBinding;
import com.sophiaxiang.wanderlust.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailsActivity extends AppCompatActivity {
    public static final String TAG = "ChatDetailsActivity";
    private ActivityChatDetailsBinding mBinding;
    private DatabaseReference mDatabase;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mMessages;
    private String mCurrentUserId;
    private String mOtherUserId;
    private String mOtherUserName;
    private String mChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getIntentExtras();
        setUpToolBar();

        getChatId();
        setUpMessagePosting();
        setUpNewMessageListener();
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        mCurrentUserId = intent.getStringExtra("current user id");
        mOtherUserId = intent.getStringExtra("other user id");
        mOtherUserName = intent.getStringExtra("other user name");
    }

    private void setUpToolBar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mOtherUserName);
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);
        this.setSupportActionBar(toolbar);
    }

    public void getChatId() {
        if (mCurrentUserId.compareTo(mOtherUserId) < 0) {
            mChatId = mCurrentUserId + mOtherUserId;
        } else {
            mChatId = mOtherUserId + mCurrentUserId;
        }
    }

    private void setUpNewMessageListener() {
        Query recentMessagesQuery = mDatabase.child("chats").child(mChatId).limitToFirst(40);
        recentMessagesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                mMessages.add(0, message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemInserted(0);
                        mBinding.rvChat.scrollToPosition(0);
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

    // Set up button event handler which posts the entered message to Parse
    void setUpMessagePosting() {
        mMessages = new ArrayList<>();
        mAdapter = new ChatMessageAdapter(ChatDetailsActivity.this, mCurrentUserId, mMessages);
        mBinding.rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
        linearLayoutManager.setReverseLayout(true);
        mBinding.rvChat.setLayoutManager(linearLayoutManager);

        setSendOnClick();
    }

    private void setSendOnClick() {
        mBinding.ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create message in individual chat history
                String data = mBinding.etMessage.getText().toString();
                ChatMessage message = new ChatMessage(mCurrentUserId, data);
                mDatabase.child("chats").child(mChatId).push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatDetailsActivity.this, "Message delivered!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to save message", e);
                            }
                        });

                // update last message contents and time in current and other users' chat lists
                mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("lastMessage").setValue(message.getMessageText());
                mDatabase.child("userChatLists").child(mCurrentUserId).child(mChatId).child("lastMessageTime").setValue(message.getMessageTime());
                mDatabase.child("userChatLists").child(mOtherUserId).child(mChatId).child("lastMessage").setValue(message.getMessageText());
                mDatabase.child("userChatLists").child(mOtherUserId).child(mChatId).child("lastMessageTime").setValue(message.getMessageTime());

                mBinding.etMessage.setText(null);
            }
        });
    }
}