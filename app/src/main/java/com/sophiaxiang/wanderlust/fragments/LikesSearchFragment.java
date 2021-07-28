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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.LikedUserAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentLikesBinding;
import com.sophiaxiang.wanderlust.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class LikesSearchFragment extends Fragment {
    public static final String TAG = "LikesSearchFragment";
    private FragmentSearchBinding mBinding;
    private LikedUserAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private List<String> mLikedUserIds;

    public LikesSearchFragment() {
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
        currentUserId = (String) bundle.getSerializable("current user id");

        mLikedUserIds = new ArrayList<>();
        mAdapter = new LikedUserAdapter(getContext(), mLikedUserIds, currentUserId);
        mBinding.rvSearches.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvSearches.setLayoutManager(linearLayoutManager);

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
                queryLikes(query);
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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }


    private void queryLikes(String query) {
        Query recentLikesQuery = mDatabase.child("likedUserLists").child(currentUserId).orderByChild("likedAt");;
        recentLikesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLikedUserIds.clear();
                mAdapter.notifyDataSetChanged();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String likedUserId = snapshot.getKey().toString();
                    checkFilter(likedUserId, query);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadChats:onCancelled", databaseError.toException());
            }
        });
    }

    // if user's name matches query, add user to the list
    private void checkFilter(String likedUserId, String query) {
        mDatabase.child("users").child(likedUserId).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue().toString().toLowerCase().contains(query.toLowerCase())) {
                    mLikedUserIds.add(0, likedUserId);
                    mAdapter.notifyItemInserted(0);
                }
                if (mLikedUserIds.size() == 0) mBinding.tvNoSearchResults.setVisibility(View.VISIBLE);
                else mBinding.tvNoSearchResults.setVisibility(View.GONE);
            }
        });
    }
}