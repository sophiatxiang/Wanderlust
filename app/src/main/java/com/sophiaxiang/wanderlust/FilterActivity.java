package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.sophiaxiang.wanderlust.databinding.ActivityFilterBinding;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    private int filterRadius;
    private boolean filterFemale;
    private boolean filterMale;
    private boolean filterGenderOther;
    private int filterAgeMin;
    private int filterAgeMax;
    private int filterVacationOverlap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        getIntentValues();
        setUpRadiusSlider();
        setUpOverlapSlider();
        setUpAgeRangeSlider();
        setUpGenderCheckBoxes();
        setUpButtons();
    }

    private void getIntentValues() {
        filterRadius = getIntent().getIntExtra("filter radius", 100);
        filterFemale = getIntent().getBooleanExtra("filter female", false);
        filterMale = getIntent().getBooleanExtra("filter male", false);
        filterGenderOther = getIntent().getBooleanExtra("filter gender other", false);
        filterAgeMin = getIntent().getIntExtra("filter age min", 18);
        filterAgeMax = getIntent().getIntExtra("filter age max", 120);
        filterVacationOverlap = getIntent().getIntExtra("filter vacation overlap", 1);
    }

    private void setUpRadiusSlider() {
        binding.tvDestinationDistance.setText(filterRadius + " mi");
        binding.sliderRadius.setValue(filterRadius);
        binding.sliderRadius.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filterRadius = (int) value;
                binding.tvDestinationDistance.setText(filterRadius + " mi");
            }
        });
    }

    private void setUpOverlapSlider() {
        binding.tvVacationOverlap.setText(filterVacationOverlap + " days");
        binding.sliderOverlap.setValue(filterVacationOverlap);
        binding.sliderOverlap.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                filterVacationOverlap = (int) value;
                binding.tvVacationOverlap.setText(filterVacationOverlap + " days");
            }
        });
    }

    private void setUpAgeRangeSlider() {
        binding.tvAgeRange.setText(filterAgeMin + " - " + filterAgeMax);
        binding.rangeSliderAge.setValues((float) filterAgeMin, (float) filterAgeMax);
        binding.rangeSliderAge.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                filterAgeMin = slider.getValues().get(0).intValue();
                filterAgeMax = slider.getValues().get(1).intValue();
                binding.tvAgeRange.setText(filterAgeMin + " - " + filterAgeMax);
            }
        });
    }

    private void setUpGenderCheckBoxes() {
        binding.cbFemale.setChecked(filterFemale);
        binding.cbMale.setChecked(filterMale);
        binding.cbGenderOther.setChecked(filterGenderOther);
    }


    public void onGenderCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbFemale:
                if (checked) filterFemale = true;
                else filterFemale = false;
                break;
            case R.id.cbMale:
                if (checked) filterMale = true;
                else filterMale = false;
                break;
            case R.id.cbGenderOther:
                if (checked) filterGenderOther = true;
                else filterGenderOther = false;
                break;
        }
    }

    private void setUpButtons() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnShowResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                output.putExtra("filter radius", filterRadius);
                if (filterFemale || filterMale || filterGenderOther) {
                    output.putExtra("filter female", filterFemale);
                    output.putExtra("filter male", filterMale);
                    output.putExtra("filter gender other", filterGenderOther);
                }
                output.putExtra("filter age min", filterAgeMin);
                output.putExtra("filter age max", filterAgeMax);
                output.putExtra("filter vacation overlap", filterVacationOverlap);
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }
}