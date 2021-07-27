package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.sophiaxiang.wanderlust.adapters.LikedUserAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentChatBinding;
import com.sophiaxiang.wanderlust.databinding.FragmentLikesBinding;
import com.sophiaxiang.wanderlust.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {
    public static final String TAG = "LikesFragment";
    private FragmentLikesBinding mBinding;
    private LikedUserAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private List<String> mLikedUserIds;

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_likes, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        currentUserId = (String) bundle.getSerializable("current user id");

        mLikedUserIds = new ArrayList<>();
        mAdapter = new LikedUserAdapter(getContext(), mLikedUserIds, currentUserId);
        mBinding.rvLikes.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvLikes.setLayoutManager(linearLayoutManager);

        queryLikes();
    }

    private void queryLikes() {
        Query recentChatsQuery = mDatabase.child("likedUserLists").child(currentUserId).orderByChild("likedAt");;
        recentChatsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLikedUserIds.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String likedUserId = snapshot.getKey().toString();
                    mLikedUserIds.add(0, likedUserId);
                }
                mAdapter.notifyDataSetChanged();
                if (mLikedUserIds.size() == 0) mBinding.tvNoLikes.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadChats:onCancelled", databaseError.toException());
            }
        });
    }
}