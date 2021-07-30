package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.databinding.ActivityGenderSetUpBinding;

public class GenderSetUpActivity extends AppCompatActivity {
    public static final String TAG = "GenderSetUpActivity";
    private static final String[] GENDER_CHOICES = {"select a gender...", "female", "male", "other"};
    private ActivityGenderSetUpBinding mBinding;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gender_set_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = getIntent().getStringExtra("user id");

        setUpSpinner();

        mBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGender.equals("")){
                    Toast.makeText(GenderSetUpActivity.this, "Please select a gender!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveGender();
                goHometownSetUp();
            }
        });
    }

    private void setUpSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, GENDER_CHOICES) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.item_spinner);
        mBinding.spinnerGenderSetUp.setAdapter(adapter);

        mBinding.spinnerGenderSetUp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText;
                switch (position) {
                    case 0:
                        mGender = "";
                        break;
                    case 1:
                    case 2:
                    case 3:
                        selectedItemText = (String) parent.getItemAtPosition(position);
                        mGender = selectedItemText;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = "";
            }
        });
    }

    private void saveGender() {
        mDatabase.child("users").child(mUserId).child("gender").setValue(mGender);
    }

    private void goHometownSetUp() {
        Intent intent = new Intent(GenderSetUpActivity.this, FromSetUpActivity.class);
        intent.putExtra("user id", mUserId);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}