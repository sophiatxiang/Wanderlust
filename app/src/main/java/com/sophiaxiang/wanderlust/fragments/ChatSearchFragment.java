package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.ChatAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentChatSearchBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatSearchFragment extends Fragment {

    public static final String TAG = "ChatFragment";
    private FragmentChatSearchBinding binding;
    private ChatAdapter mAdapter;
    private DatabaseReference mDatabase;
    private List<Chat> allChats;
    private String currentUserId;

    public ChatSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_search, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        currentUserId = (String) bundle.getSerializable("current user id");

        allChats = new ArrayList<>();
        mAdapter = new ChatAdapter(getContext(), allChats);
        binding.rvSearchedChats.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvSearchedChats.setLayoutManager(linearLayoutManager);

        setUpToolBar(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate( R.menu.menu_search, menu);
        final MenuItem cancelItem = menu.findItem(R.id.action_cancel);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                queryChats(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        cancelItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getActivity().getSupportFragmentManager().popBackStack();
                return false;
            }
        });
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(null);
    }

    private void queryChats(String query) {
        Query recentChatsQuery = mDatabase.child("userChatLists").child(currentUserId).limitToFirst(40).orderByChild("lastMessageTime");;
        recentChatsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allChats.clear();
                for (DataSnapshot chatSnapshot: dataSnapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    if (chat.getLastMessageTime() != 0 && chat.getOtherUserName().toLowerCase().contains(query.toLowerCase())) {
                        allChats.add(0, chat);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (allChats.size() == 0) binding.tvNoChatResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadChats:onCancelled", databaseError.toException());
            }
        });
    }
}