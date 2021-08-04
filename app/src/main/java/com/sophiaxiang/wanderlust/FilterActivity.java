package com.sophiaxiang.wanderlust;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.sophiaxiang.wanderlust.databinding.ActivityFilterBinding;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding mBinding;
    private int mFilterRadius;
    private boolean mFilterFemale;
    private boolean mFilterMale;
    private boolean mFilterGenderOther;
    private int mFilterAgeMin;
    private int mFilterAgeMax;
    private int mFilterVacationOverlap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        getIntentValues();
        setUpRadiusSlider();
        setUpOverlapSlider();
        setUpAgeRangeSlider();
        setUpGenderCheckBoxes();
        setUpButtons();
    }

    private void getIntentValues() {
        mFilterRadius = getIntent().getIntExtra("filter radius", 100);
        mFilterFemale = getIntent().getBooleanExtra("filter female", false);
        mFilterMale = getIntent().getBooleanExtra("filter male", false);
        mFilterGenderOther = getIntent().getBooleanExtra("filter gender other", false);
        mFilterAgeMin = getIntent().getIntExtra("filter age min", 18);
        mFilterAgeMax = getIntent().getIntExtra("filter age max", 120);
        mFilterVacationOverlap = getIntent().getIntExtra("filter vacation overlap", 1);
    }

    private void setUpRadiusSlider() {
        mBinding.tvDestinationDistance.setText(mFilterRadius + " mi");
        mBinding.sliderRadius.setValue(mFilterRadius);
        mBinding.sliderRadius.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mFilterRadius = (int) value;
                mBinding.tvDestinationDistance.setText(mFilterRadius + " mi");
            }
        });
    }

    private void setUpOverlapSlider() {
        mBinding.tvVacationOverlap.setText(mFilterVacationOverlap + " days");
        mBinding.sliderOverlap.setValue(mFilterVacationOverlap);
        mBinding.sliderOverlap.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                mFilterVacationOverlap = (int) value;
                mBinding.tvVacationOverlap.setText(mFilterVacationOverlap + " days");
            }
        });
    }

    private void setUpAgeRangeSlider() {
        mBinding.tvAgeRange.setText(mFilterAgeMin + " - " + mFilterAgeMax);
        mBinding.rangeSliderAge.setValues((float) mFilterAgeMin, (float) mFilterAgeMax);
        mBinding.rangeSliderAge.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                mFilterAgeMin = slider.getValues().get(0).intValue();
                mFilterAgeMax = slider.getValues().get(1).intValue();
                mBinding.tvAgeRange.setText(mFilterAgeMin + " - " + mFilterAgeMax);
            }
        });
    }

    private void setUpGenderCheckBoxes() {
        mBinding.cbFemale.setChecked(mFilterFemale);
        mBinding.cbMale.setChecked(mFilterMale);
        mBinding.cbGenderOther.setChecked(mFilterGenderOther);
    }

    public void onGenderCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.cbFemale:
                mFilterFemale = checked;
                break;
            case R.id.cbMale:
                mFilterMale = checked;
                break;
            case R.id.cbGenderOther:
                mFilterGenderOther = checked;
                break;
        }
    }

    private void setUpButtons() {
        mBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_down);
            }
        });

        mBinding.btnShowResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                output.putExtra("filter radius", mFilterRadius);
                if (mFilterFemale || mFilterMale || mFilterGenderOther) {
                    output.putExtra("filter female", mFilterFemale);
                    output.putExtra("filter male", mFilterMale);
                    output.putExtra("filter gender other", mFilterGenderOther);
                }
                output.putExtra("filter age min", mFilterAgeMin);
                output.putExtra("filter age max", mFilterAgeMax);
                output.putExtra("filter vacation overlap", mFilterVacationOverlap);
                setResult(RESULT_OK, output);
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_down);
            }
        });
    }
}