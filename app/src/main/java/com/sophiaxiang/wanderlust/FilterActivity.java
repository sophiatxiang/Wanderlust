package com.sophiaxiang.wanderlust;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.sophiaxiang.wanderlust.databinding.ActivityFilterBinding;

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    private int filterRadius;
    private boolean filterFemale;
    private boolean filterMale;
    private boolean filterGenderOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        filterRadius = getIntent().getIntExtra("filter radius", 100);
        filterRadius = getIntent().getIntExtra("filter radius", 100);
        filterFemale = getIntent().getBooleanExtra("filter female", false);
        filterMale = getIntent().getBooleanExtra("filter male", false);
        filterGenderOther = getIntent().getBooleanExtra("filter gender other", false);

        setUpSeekBar();
        setUpGenderCheckBoxes();
        setUpButtons();
    }

    private void setUpGenderCheckBoxes() {
        binding.cbFemale.setChecked(filterFemale);
        binding.cbMale.setChecked(filterMale);
        binding.cbGenderOther.setChecked(filterGenderOther);
    }

    private void setUpSeekBar() {
        binding.tvDestinationDistance.setText(filterRadius + " mi");
        binding.seekBar.setProgress(filterRadius / 5);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filterRadius = progress * 5;
                binding.tvDestinationDistance.setText(filterRadius + " mi");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }

    public void onGenderCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbFemale:
                if (checked) filterFemale = true;

            else
                filterFemale = false;
                break;
            case R.id.cbMale:
                if (checked)
                    filterMale = true;
            else
                filterMale = false;
                break;
            case R.id.cbGenderOther:
                if (checked)
                    filterGenderOther = true;
                else
                    filterGenderOther = false;
                break;
        }
    }
}