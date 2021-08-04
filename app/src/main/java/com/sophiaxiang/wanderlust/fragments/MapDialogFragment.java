package com.sophiaxiang.wanderlust.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentMapDialogBinding;

public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {
    private FragmentMapDialogBinding mBinding;
    private Double mLatitude;
    private Double mLongitude;
    private String mDestination;

    public MapDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static MapDialogFragment newInstance(String destination, Double latitude, Double longitude) {
        MapDialogFragment frag = new MapDialogFragment();
        Bundle args = new Bundle();
        args.putString("destination", destination);
        args.putDouble("lat", latitude);
        args.putDouble("long", longitude);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map_dialog, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        mLatitude = getArguments().getDouble("lat");
        mLongitude = getArguments().getDouble("long");
        mDestination = getArguments().getString("destination");

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.97);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
        getDialog().getWindow().setLayout(width, height);

        mBinding.mapDestination.onCreate(savedInstanceState);
        mBinding.mapDestination.onResume();
        mBinding.mapDestination.getMapAsync(this);

        mBinding.ibCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng userDestination = new LatLng(mLatitude, mLongitude);
        googleMap.addMarker(new MarkerOptions()
                .position(userDestination)
                .title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userDestination));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userDestination, 15.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
    }
}