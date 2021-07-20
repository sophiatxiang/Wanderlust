package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentMyVacationBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;

public class MyVacationFragment extends Fragment {

    public static final String TAG = "MyVacationFragment";
    private FragmentMyVacationBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private Vacation vacation;


    public MyVacationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_vacation, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        vacation = (Vacation) bundle.getSerializable("vacation");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        populateViews();


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabaseVacation();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void populateViews() {
        binding.tvDestination.setText(vacation.getDestination());
        binding.etStartDate.setText(vacation.getStartDate());
        binding.etEndDate.setText(vacation.getEndDate());
        binding.etNotes.setText(vacation.getNotes());
    }

    private void updateDatabaseVacation() {
        String destination = binding.tvDestination.getText().toString();
        String startDate = binding.etStartDate.getText().toString();
        String endDate = binding.etEndDate.getText().toString();
        String notes = binding.etNotes.getText().toString();
        Vacation vacation = new Vacation(firebaseUser.getUid(), destination, startDate, endDate, notes);
        mDatabase.child("users").child(firebaseUser.getUid()).child("vacation").setValue(vacation);
        Toast.makeText(getContext(), "Vacation details saved!", Toast.LENGTH_SHORT).show();
    }
}