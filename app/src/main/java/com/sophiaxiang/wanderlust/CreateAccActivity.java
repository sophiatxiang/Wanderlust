package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sophiaxiang.wanderlust.databinding.ActivityCreateAccBinding;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

public class CreateAccActivity extends AppCompatActivity {

    public static final String TAG = "CreateAccActivity";
    private FirebaseAuth mAuth;
    private ActivityCreateAccBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_acc);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        binding.btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                createAccount(email, password);
            }
        });
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            firebaseUser = mAuth.getCurrentUser();
                            createDatabaseUserProfile(firebaseUser.getUid());
                            createDatabaseUserVacation(firebaseUser.getUid());
                            goNameSetUpActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createDatabaseUserProfile(String userId) {
        User user = new User(userId);
        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateAccActivity.this, "successfully created user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "user write failure", e);
                    }
                });
    }

    private void createDatabaseUserVacation(String userId) {
        Vacation vacation = new Vacation(userId);
        mDatabase.child("users").child(userId).child("vacation").setValue(vacation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateAccActivity.this, "vacation write successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "vacation write failure", e);
                    }
                });
    }


    private void goNameSetUpActivity() {
        Intent intent = new Intent(CreateAccActivity.this, NameSetUpActivity.class);
        intent.putExtra("user id", firebaseUser.getUid());
        startActivity(intent);
        finish();
    }
}