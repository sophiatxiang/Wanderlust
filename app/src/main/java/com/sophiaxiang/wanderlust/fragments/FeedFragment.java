package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.sophiaxiang.wanderlust.FilterActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.ChatAdapter;
import com.sophiaxiang.wanderlust.adapters.UserAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentFeedBinding;
import com.sophiaxiang.wanderlust.databinding.FragmentProfileBinding;
import com.sophiaxiang.wanderlust.models.Chat;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private static int FILTER_REQUEST_CODE = 28;
    private FragmentFeedBinding binding;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private UserAdapter mAdapter;
    private List<User> users;
    private User currentUser;

    private int filterRadius;
    private boolean filterFemale;
    private boolean filterMale;
    private boolean filterGenderOther;
    private int filterAgeMin;
    private int filterAgeMax;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        currentUserId = (String) bundle.getSerializable("current user id");
        filterRadius = 100;
        filterAgeMin = 18;
        filterAgeMax = 120;

        users = new ArrayList<>();
        mAdapter = new UserAdapter(getContext(), users);
        binding.rvUsers.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvUsers.setLayoutManager(linearLayoutManager);

        getCurrentUser();
        //queryUsers();

        binding.tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFilterActivity();
                //TODO: add animation slide up from bottom
            }
        });
    }

    private void getCurrentUser() {
        mDatabase.child("users").child(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser.getVacation().getDestination().equals("")) {
                    queryFilteredUsers(Integer.MAX_VALUE);
                }
                else {
                    queryFilteredUsers(filterRadius);
                }
            }
        });
    }

    private void goFilterActivity() {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        intent.putExtra("filter radius", filterRadius);
        if (filterFemale || filterMale || filterGenderOther) {
            intent.putExtra("filter female", filterFemale);
            intent.putExtra("filter male", filterMale);
            intent.putExtra("filter gender other", filterGenderOther);
        }
        intent.putExtra("filter age min", filterAgeMin);
        intent.putExtra("filter age max", filterAgeMax);
        startActivityForResult(intent, FILTER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                filterRadius = data.getIntExtra("filter radius", 100);
                filterFemale = data.getBooleanExtra("filter female", false);
                filterMale = data.getBooleanExtra("filter male", false);
                filterGenderOther = data.getBooleanExtra("filter gender other", false);
                filterAgeMin = data.getIntExtra("filter age min", 18);
                filterAgeMax = data.getIntExtra("filter age max", 120);
                queryFilteredUsers(filterRadius);
            }
        }
    }

    private void queryFilteredUsers(int radius) {
        Query usersQuery = mDatabase.child("users").limitToFirst(40);
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                mAdapter.notifyDataSetChanged();
                binding.tvNumResults.setText(users.size() + " RESULTS");
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    // check if queried user is current user
                    if (!user.getUserId().equals(currentUserId)) {
                        boolean filteredGender = checkGender(user);
                        boolean filteredAge = checkAge(user);
                        if (filteredGender && filteredAge) {
                            getFilteredVacation(user, radius);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadChats:onCancelled", error.toException());
            }
        });
    }

    private boolean checkGender(User user) {
        if ((filterFemale && filterMale && filterGenderOther) || (!filterFemale && !filterMale && !filterGenderOther)) {
            return true;
        }
        else if (filterFemale && user.getGender().equals("female")) {
            return true;
        }
        else if (filterMale && user.getGender().equals("male")) {
            return true;
        }
        else if (filterGenderOther && user.getGender().equals("other")) {
            return true;
        }
        else return false;
    }

    private boolean checkAge(User user) {
        if ((user.getAge() >= filterAgeMin) && (user.getAge() <= filterAgeMax))
            return true;
        else
            return false;
    }

    private void getFilteredVacation(User user, int radius) {
        mDatabase.child("users").child(user.getUserId()).child("vacation").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Vacation vacation = task.getResult().getValue(Vacation.class);

                    boolean withinRadius = checkDistance(vacation, radius);
                    if (withinRadius) {
                        user.setVacation(vacation);
                        users.add(0, user);
                        mAdapter.notifyItemInserted(0);
                        binding.tvNumResults.setText(users.size() + " RESULTS");
                    }
                }
                else {
                    Log.e(TAG, "Error getting vacation data", task.getException());
                }
            }
        });
    }

    private boolean checkDistance(Vacation vacation, int radius) {
        LatLng latLng1 = new LatLng(vacation.getLatitude(), vacation.getLongitude());
        LatLng latLng2 = new LatLng(currentUser.getVacation().getLatitude(), currentUser.getVacation().getLongitude());
        double distanceMiles = SphericalUtil.computeDistanceBetween(latLng1, latLng2) * 0.000621371192;
        if ((int) distanceMiles <= radius)
            return true;
        else return false;
    }
}