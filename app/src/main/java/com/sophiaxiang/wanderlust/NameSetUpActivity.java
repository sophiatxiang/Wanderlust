package com.sophiaxiang.wanderlust;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.databinding.ActivityNameSetUpBinding;
import com.sophiaxiang.wanderlust.models.User;

public class NameSetUpActivity extends AppCompatActivity {

    public static final String TAG = "NameSetUpActivity";
    private ActivityNameSetUpBinding mBinding;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_name_set_up);

        userId = getIntent().getStringExtra("user id");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.etNameSetUp.getText().toString().equals("")) {
                    Toast.makeText(NameSetUpActivity.this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUserName();
                goAgeSetUp();
            }
        });
    }

    private void saveUserName() {
        mDatabase.child("users").child(userId).child("name").setValue(mBinding.etNameSetUp.getText().toString());
    }

    private void goAgeSetUp() {
        Intent intent = new Intent(NameSetUpActivity.this, AgeSetUpActivity.class);
        intent.putExtra("user id", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}