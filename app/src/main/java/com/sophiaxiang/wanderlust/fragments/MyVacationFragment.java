package com.sophiaxiang.wanderlust.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentMyVacationBinding;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyVacationFragment extends Fragment {

    public static final String TAG = "MyVacationFragment";
    private FragmentMyVacationBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private Vacation vacation;
    private PlacesClient placesClient;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


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

        // Initialize the SDK
        Places.initialize(getContext(), "AIzaSyDtWeLjyQ4g1uU9oow9HDjBX7T0AjPc9pQ");
        // Create a new PlacesClient instance
        placesClient = Places.createClient(getContext());

        Bundle bundle = getArguments();
        vacation = (Vacation) bundle.getSerializable("vacation");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        populateViews();
        setUpDatePicker();


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabaseVacation();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.tvEditDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutocompleteSessionToken.newInstance();
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private void setUpDatePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");

        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> materialDatePicker = materialDateBuilder.build();

        binding.tvStartDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        binding.tvEndDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                String startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date(selection.first));
                String endDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date(selection.second));

                binding.tvStartDate.setText(startDate);
                binding.tvEndDate.setText(endDate);
            }
        });
    }

    private void populateViews() {
        binding.tvDestination.setText(vacation.getDestination());
        binding.tvStartDate.setText(vacation.getStartDate());
        binding.tvEndDate.setText(vacation.getEndDate());
        binding.etNotes.setText(vacation.getNotes());
    }

    private void updateDatabaseVacation() {
        String destination = binding.tvDestination.getText().toString();
        String startDate = binding.tvStartDate.getText().toString();
        String endDate = binding.tvEndDate.getText().toString();
        String notes = binding.etNotes.getText().toString();
        Vacation vacation = new Vacation(firebaseUser.getUid(), destination, startDate, endDate, notes);
        mDatabase.child("users").child(firebaseUser.getUid()).child("vacation").setValue(vacation);
        Toast.makeText(getContext(), "Vacation details saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName());
                binding.tvDestination.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}