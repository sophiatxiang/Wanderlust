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
import android.widget.Toast;

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
import com.sophiaxiang.wanderlust.fragments.EditProfileFragment;
import com.sophiaxiang.wanderlust.fragments.FeedFragment;
import com.sophiaxiang.wanderlust.fragments.MyVacationFragment;
import com.sophiaxiang.wanderlust.fragments.ProfileFragment;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private User currentUser;
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
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        Bundle feedBundle = new Bundle();
                        feedBundle.putSerializable("current user id", currentUserId);
                        fragment.setArguments(feedBundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_chat:
                        fragment = new ChatFragment();
                        Bundle chatBundle = new Bundle();
                        chatBundle.putSerializable("current user id", currentUserId);
                        fragment.setArguments(chatBundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_profile:
//                        fragmentManager.beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();
                        fragment = new ProfileFragment();
                        Bundle profileBundle = new Bundle();
                        profileBundle.putSerializable("current user", currentUser);
                        profileBundle.putSerializable("vacation", currentUserVacation);
                        fragment.setArguments(profileBundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                }
                return true;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.action_home);

        mDatabase.child("userChatLists").child(currentUserId).child("3WWVbfjsWYQ41r51noT6yvwfe7624Fn0b6gnOpXzgvsMDl6jmexXk562").child("");

    }

    private void getCurrentUserVacation() {
        DatabaseReference vacationDetailsReference = mDatabase.child("users").child(currentUserId).child("vacation");
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
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserReference.addValueEventListener(userListener);
    }
}