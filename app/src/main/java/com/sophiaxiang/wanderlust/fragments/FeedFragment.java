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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.sophiaxiang.wanderlust.FilterActivity;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.adapters.UserAdapter;
import com.sophiaxiang.wanderlust.databinding.FragmentFeedBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.time.temporal.ChronoUnit.DAYS;

public class FeedFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private static final int FILTER_REQUEST_CODE = 28;

    private FragmentFeedBinding mBinding;
    private DatabaseReference mDatabase;
    private UserAdapter mAdapter;
    private List<User> mUsers;
    private User mCurrentUser;
    private String mCurrentUserId;

    private int mFilterRadius;
    private boolean mFilterFemale;
    private boolean mFilterMale;
    private boolean mFilterGenderOther;
    private int mFilterAgeMin;
    private int mFilterAgeMax;
    private int mFilterVacationOverlap;

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getArguments();
        mCurrentUserId = (String) bundle.getSerializable("current user id");

        mFilterRadius = 100;
        mFilterAgeMin = 18;
        mFilterAgeMax = 120;
        mFilterVacationOverlap = 1;

        mUsers = new ArrayList<>();
        mAdapter = new UserAdapter(getContext(), mUsers, mCurrentUserId);
        mBinding.rvUsers.setAdapter(mAdapter);
        ((SimpleItemAnimator) mBinding.rvUsers.getItemAnimator()).setSupportsChangeAnimations(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvUsers.setLayoutManager(linearLayoutManager);

        getCurrentUser();
        setUpToolBar(view);

        mBinding.tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFilterActivity();
            }
        });
    }

    private void getCurrentUser() {
        mDatabase.child("users").child(mCurrentUserId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                queryFilteredUsers();
            }
        });
    }

    private void setUpToolBar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void goFilterActivity() {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        intent.putExtra("filter radius", mFilterRadius);
        if (mFilterFemale || mFilterMale || mFilterGenderOther) {
            intent.putExtra("filter female", mFilterFemale);
            intent.putExtra("filter male", mFilterMale);
            intent.putExtra("filter gender other", mFilterGenderOther);
        }
        intent.putExtra("filter age min", mFilterAgeMin);
        intent.putExtra("filter age max", mFilterAgeMax);
        intent.putExtra("filter vacation overlap", mFilterVacationOverlap);
        startActivityForResult(intent, FILTER_REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK) {
            mFilterRadius = data.getIntExtra("filter radius", 100);
            mFilterFemale = data.getBooleanExtra("filter female", false);
            mFilterMale = data.getBooleanExtra("filter male", false);
            mFilterGenderOther = data.getBooleanExtra("filter gender other", false);
            mFilterAgeMin = data.getIntExtra("filter age min", 18);
            mFilterAgeMax = data.getIntExtra("filter age max", 120);
            mFilterVacationOverlap = data.getIntExtra("filter vacation overlap", 1);
            queryFilteredUsers();
        }
    }

    private void queryFilteredUsers() {
        if (mCurrentUser.getVacation().getDestination().equals("") || mCurrentUser.getVacation().getStartDate().equals("")) {
            mBinding.tvNoVacationDetails.setVisibility(View.VISIBLE);
            return;
        }
        // age filter is built into the database query
        Query usersQuery = mDatabase.child("users").limitToFirst(40).orderByChild("age").startAt(mFilterAgeMin).endAt(mFilterAgeMax);
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                mAdapter.notifyDataSetChanged();
                mBinding.tvNumResults.setText(mUsers.size() + " RESULTS");
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    // check if queried user is current user
                    if (!user.getUserId().equals(mCurrentUserId) && withinGenderFilter(user)) {
                        getFilteredVacation(user, mFilterRadius);
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
        if ((mFilterFemale && mFilterMale && mFilterGenderOther) || (!mFilterFemale && !mFilterMale && !mFilterGenderOther)) {
            return true;
        }
        return (mFilterFemale && user.getGender().equals("female")) ||
                (mFilterMale && user.getGender().equals("male")) ||
                (mFilterGenderOther && user.getGender().equals("other"));
    }

    private void getFilteredVacation(User user, int radius) {
        mDatabase.child("users").child(user.getUserId()).child("vacation").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            Vacation vacation = task.getResult().getValue(Vacation.class);
                            // if vacation passes filters, add the user into the List and notify the adapter
                            if (withinVacationFilters(vacation, radius)) {
                                user.setVacation(vacation);
                                mUsers.add(0, user);
                                mAdapter.notifyItemInserted(0);
                                mBinding.tvNumResults.setText(mUsers.size() + " RESULTS");
                            }
                        } else {
                            Log.e(TAG, "Error getting vacation data", task.getException());
                        }
                    }
                });
    }

    private boolean withinVacationFilters(Vacation vacation, int radius) {
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
        LatLng latLng2 = new LatLng(mCurrentUser.getVacation().getLatitude(), mCurrentUser.getVacation().getLongitude());
        double distanceMiles = SphericalUtil.computeDistanceBetween(latLng1, latLng2) * 0.000621371192;
        return (int) distanceMiles <= radius;
    }

    private boolean checkVacationOverlap(Vacation vacation) throws ParseException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        if (vacation.getStartDate().equals("")) return false;
        LocalDate myStartDate = LocalDate.parse(mCurrentUser.getVacation().getStartDate(), format);
        LocalDate myEndDate = LocalDate.parse(mCurrentUser.getVacation().getEndDate(), format);
        LocalDate otherStartDate = LocalDate.parse(vacation.getStartDate(), format);
        LocalDate otherEndDate = LocalDate.parse(vacation.getEndDate(), format);
        if (DAYS.between(otherStartDate, otherEndDate) + 1 < mFilterVacationOverlap) return false;
        if ((otherStartDate.compareTo(myStartDate) <= 0) && (otherEndDate.compareTo(myStartDate.plusDays(mFilterVacationOverlap - 1)) >= 0)) {
            return true;
        }
        return otherStartDate.compareTo(myStartDate) > 0 && otherStartDate.compareTo(myEndDate.minusDays(mFilterVacationOverlap - 1)) <= 0;
    }
}