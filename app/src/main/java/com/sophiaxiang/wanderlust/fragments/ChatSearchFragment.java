package com.sophiaxiang.wanderlust.fragments;

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
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import com.sophiaxiang.wanderlust.databinding.FragmentSearchBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatSearchFragment extends Fragment {
    public static final String TAG = "ChatSearchFragment";
    private FragmentSearchBinding mBinding;
    private ChatAdapter mAdapter;
    private DatabaseReference mDatabase;
    private List<Chat> mChats;
    private String mCurrentUserId;

    public ChatSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        mCurrentUserId = (String) bundle.getSerializable("current user id");

        mChats = new ArrayList<>();
        mAdapter = new ChatAdapter(getContext(), mChats);
        mBinding.rvSearches.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvSearches.setLayoutManager(linearLayoutManager);

        setUpToolBar(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
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

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(null);
    }

    private void queryChats(String query) {
        Query recentChatsQuery = mDatabase.child("userChatLists").child(mCurrentUserId).limitToFirst(40).orderByChild("lastMessageTime");
        recentChatsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChats.clear();
                mBinding.tvNoSearchResults.setVisibility(View.GONE);
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    if (chat.getLastMessageTime() != 0 && chat.getOtherUserName().toLowerCase().contains(query.toLowerCase())) {
                        mChats.add(0, chat);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mChats.size() == 0) mBinding.tvNoSearchResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadChats:onCancelled", databaseError.toException());
            }
        });
    }
}