package com.sophiaxiang.wanderlust.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.ChatAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentChatBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    public static final String TAG = "ChatFragment";
    private FragmentChatBinding mBinding;
    private ChatAdapter mAdapter;
    private DatabaseReference mDatabase;
    private List<Chat> mChats;
    private String mCurrentUserId;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        mCurrentUserId = (String) bundle.getSerializable("current user id");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mChats = new ArrayList<>();
        mAdapter = new ChatAdapter(getContext(), mChats);
        mBinding.rvChats.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvChats.setLayoutManager(linearLayoutManager);

        setUpToolbar(view);
        queryChats();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_go_search, menu);
        MenuItem search = menu.findItem(R.id.action_launch_search);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goChatSearchFrag();
                return true;
            }
        });

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
    }

    private void goChatSearchFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new ChatSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current user id", mCurrentUserId);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setUpToolbar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Chats");
        // hide app logo title
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void queryChats() {
        Query recentChatsQuery = mDatabase.child("userChatLists").child(mCurrentUserId).limitToFirst(40).orderByChild("lastMessageTime");
        recentChatsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChats.clear();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    if (chat.getLastMessageTime() != 0) {
                        mChats.add(0, chat);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mChats.size() == 0) {
                    mBinding.tvNoChats.setVisibility(View.VISIBLE);
                    mBinding.rvChats.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadChats:onCancelled", databaseError.toException());
            }
        });
    }
}