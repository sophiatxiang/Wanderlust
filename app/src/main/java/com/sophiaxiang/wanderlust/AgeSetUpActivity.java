package com.sophiaxiang.wanderlust;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.databinding.ActivityAgeSetUpBinding;

public class AgeSetUpActivity extends AppCompatActivity {

    public static final String TAG = "AgeSetUpActivity";
    private ActivityAgeSetUpBinding mBinding;
    private DatabaseReference mDatabase;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_age_set_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = getIntent().getStringExtra("user id");

        mBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mBinding.etAgeSetUp.toString())){
                    Toast.makeText(AgeSetUpActivity.this, "Please enter your age!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveAge();
                goGenderSetUp();
            }
        });
    }

    private void saveAge() {
        mDatabase.child("users").child(mUserId).child("age").setValue(Integer.parseInt(mBinding.etAgeSetUp.getText().toString()));
    }

    private void goGenderSetUp() {
        Intent intent = new Intent(AgeSetUpActivity.this, GenderSetUpActivity.class);
        intent.putExtra("user id", mUserId);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}