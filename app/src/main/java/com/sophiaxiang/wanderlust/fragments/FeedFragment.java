package com.sophiaxiang.wanderlust.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static java.time.temporal.ChronoUnit.DAYS;

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
    private int filterVacationOverlap;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        filterVacationOverlap = 1;

        users = new ArrayList<>();
        mAdapter = new UserAdapter(getContext(), users, currentUserId);
        binding.rvUsers.setAdapter(mAdapter);
        ((SimpleItemAnimator) binding.rvUsers.getItemAnimator()).setSupportsChangeAnimations(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvUsers.setLayoutManager(linearLayoutManager);

        getCurrentUser();
        setUpToolBar(view);

        binding.tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFilterActivity();
            }
        });
    }

    private void getCurrentUser() {
        mDatabase.child("users").child(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                queryFilteredUsers(filterRadius);
            }
        });
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        intent.putExtra("filter vacation overlap", filterVacationOverlap);
        startActivityForResult(intent, FILTER_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
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
                filterVacationOverlap = data.getIntExtra("filter vacation overlap", 1);
                queryFilteredUsers(filterRadius);
            }
        }
    }

    private void queryFilteredUsers(int radius) {
        if (currentUser.getVacation().getDestination().equals("") || currentUser.getVacation().getStartDate().equals("")) {
            binding.tvNoVacationDetails.setVisibility(View.VISIBLE);
            return;
        }
        // age filter is built into the database query
        Query usersQuery = mDatabase.child("users").limitToFirst(40).orderByChild("age").startAt(filterAgeMin).endAt(filterAgeMax);
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
                        if (withinGenderFilter(user))
                            getFilteredVacation(user, radius);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadChats:onCancelled", error.toException());
            }
        });
    }

    private boolean withinGenderFilter(User user) {
        if ((filterFemale && filterMale && filterGenderOther) || (!filterFemale && !filterMale && !filterGenderOther)) {
            return true;
        }
        return (filterFemale && user.getGender().equals("female")) ||
                (filterMale && user.getGender().equals("male")) ||
                (filterGenderOther && user.getGender().equals("other"));
    }

    private void getFilteredVacation(User user, int radius) {
        mDatabase.child("users").child(user.getUserId()).child("vacation").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Vacation vacation = task.getResult().getValue(Vacation.class);
                    if (withinVacationFilters(vacation, radius)) {
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

    private boolean withinVacationFilters(Vacation vacation, int radius){
        if (!checkDestinationRadius(vacation, radius)) return false;
        try {
            if (!checkVacationOverlap(vacation)) return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkDestinationRadius(Vacation vacation, int radius) {
        if (vacation.getDestination().equals("")) return false;
        LatLng latLng1 = new LatLng(vacation.getLatitude(), vacation.getLongitude());
        LatLng latLng2 = new LatLng(currentUser.getVacation().getLatitude(), currentUser.getVacation().getLongitude());
        double distanceMiles = SphericalUtil.computeDistanceBetween(latLng1, latLng2) * 0.000621371192;
        if ((int) distanceMiles <= radius) return true;
        return false;
    }

    private boolean checkVacationOverlap(Vacation vacation) throws ParseException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if (vacation.getStartDate().equals("")) return false;
        LocalDate myStartDate = LocalDate.parse(currentUser.getVacation().getStartDate(), format);
        LocalDate myEndDate = LocalDate.parse(currentUser.getVacation().getEndDate(), format);
        LocalDate otherStartDate = LocalDate.parse(vacation.getStartDate(), format);
        LocalDate otherEndDate = LocalDate.parse(vacation.getEndDate(), format);
        if (DAYS.between(otherStartDate, otherEndDate) + 1 < filterVacationOverlap) return false;
        if ((otherStartDate.compareTo(myStartDate) <= 0) && (otherEndDate.compareTo(myStartDate.plusDays(filterVacationOverlap - 1)) >= 0)) {
            return true;
        }
        if (otherStartDate.compareTo(myStartDate) > 0 && otherStartDate.compareTo(myEndDate.minusDays(filterVacationOverlap - 1)) <= 0) {
            return true;
        }
        return false;
    }
}