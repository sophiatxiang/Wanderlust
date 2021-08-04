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
import com.sophiaxiang.wanderlust.adapters.LikedUserAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentLikesBinding;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {
    public static final String TAG = "LikesFragment";
    private FragmentLikesBinding mBinding;
    private LikedUserAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String mCurrentUserId;
    private List<String> mLikedUserIds;

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_likes, container, false);
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        mCurrentUserId = (String) bundle.getSerializable("current user id");

        mLikedUserIds = new ArrayList<>();
        mAdapter = new LikedUserAdapter(getContext(), mLikedUserIds, mCurrentUserId);
        mBinding.rvLikes.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvLikes.setLayoutManager(linearLayoutManager);

        setUpToolBar(view);
        queryLikes();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_go_search, menu);
        final MenuItem search = menu.findItem(R.id.action_launch_search);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goLikesSearchFrag();
                return true;
            }
        });
        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Likes");
        toolbar.findViewById(R.id.title).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void queryLikes() {
        Query recentLikesQuery = mDatabase.child("likedUserLists").child(mCurrentUserId).orderByChild("likedAt");
        recentLikesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLikedUserIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String likedUserId = snapshot.getKey();
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

    private void goLikesSearchFrag() {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        Fragment fragment = new LikesSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("current user id", mCurrentUserId);
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}