package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sophiaxiang.wanderlust.databinding.ActivityMainBinding;
import com.sophiaxiang.wanderlust.fragments.ChatFragment;
import com.sophiaxiang.wanderlust.fragments.FeedFragment;
import com.sophiaxiang.wanderlust.fragments.MyVacationFragment;
import com.sophiaxiang.wanderlust.fragments.ProfileFragment;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public User theCurrentUser;
    public Vacation currentUserVacation;
    public String currentUserId;
    public DatabaseReference mDatabase;
    public StorageReference mStorage;
    private ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        getCurrentUser();
        getCurrentUserVacation();

        binding.bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, new FeedFragment()).commit();
                        break;
                    case R.id.action_chat:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, new ChatFragment()).commit();
                        break;
                    case R.id.action_profile:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();
                        break;
                }
                return true;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    private void getCurrentUserVacation() {
        DatabaseReference vacationDetailsReference = mDatabase.child("vacations").child(currentUserId);
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                currentUserVacation = dataSnapshot.getValue(Vacation.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        vacationDetailsReference.addValueEventListener(vacationListener);
    }

    private void getCurrentUser() {
        DatabaseReference currentUserReference = mDatabase.child("users").child(currentUserId);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                theCurrentUser = dataSnapshot.getValue(User.class);
                theCurrentUser.setImageUriList(new ArrayList<>());
                getImageUris();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserReference.addValueEventListener(userListener);
    }


    private void getImageUris() {
        StorageReference imageReference1 = mStorage.child(currentUserId).child("image1.jpg");
        imageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    theCurrentUser.getImageUriList().add(uri.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "failed to get image 1: ", exception);
            }
        });

        StorageReference imageReference2 = mStorage.child(currentUserId).child("image2.jpg");
        imageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    theCurrentUser.getImageUriList().add(uri.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "failed to get image 2: ", exception);
            }
        });

        StorageReference imageReference3 = mStorage.child(currentUserId).child("image3.jpg");
        imageReference3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    theCurrentUser.getImageUriList().add(uri.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "failed to get image 3: ", exception);
            }
        });
    }
}