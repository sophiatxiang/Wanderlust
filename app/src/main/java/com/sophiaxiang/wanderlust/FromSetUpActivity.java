package com.sophiaxiang.wanderlust;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.databinding.ActivityFromSetUpBinding;

public class FromSetUpActivity extends AppCompatActivity {
    private ActivityFromSetUpBinding mBinding;
    private DatabaseReference mDatabase;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_from_set_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = getIntent().getStringExtra("user id");

        mBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.etFromSetUp.getText().toString().equals("")) {
                    Toast.makeText(FromSetUpActivity.this, "Please enter what city/state/country/region you are from!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveFrom();
                goMainActivity();
            }
        });
    }

    private void saveFrom() {
        mDatabase.child("users").child(mUserId).child("from").setValue(mBinding.etFromSetUp.getText().toString());
    }

    private void goMainActivity() {
        Intent intent = new Intent(FromSetUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}