package com.sophiaxiang.wanderlust;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.sophiaxiang.wanderlust.fragments.LikesFragment;
import com.sophiaxiang.wanderlust.fragments.ProfileFragment;
import com.sophiaxiang.wanderlust.models.User;
import com.sophiaxiang.wanderlust.models.Vacation;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ActivityMainBinding mBinding;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private User mCurrentUser;
    private Vacation mCurrentUserVacation;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        getCurrentUser();
        setUpBottomNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showBottomSheetDialog();
                return true;
            }
        });
        return true;
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_settings);

        TextView logout = bottomSheetDialog.findViewById(R.id.tvLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                goLoginActivity();
            }
        });
        bottomSheetDialog.show();
    }

    private void getCurrentUserVacation() {
        DatabaseReference vacationDetailsReference = mDatabase.child("users").child(mCurrentUserId).child("vacation");
        ValueEventListener vacationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Vacation object and use the values to update the UI
                mCurrentUserVacation = dataSnapshot.getValue(Vacation.class);
                mCurrentUser.setVacation(mCurrentUserVacation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "load Vacation:onCancelled", databaseError.toException());
            }
        };
        vacationDetailsReference.addValueEventListener(vacationListener);
    }

    private void getCurrentUser() {
        DatabaseReference currentUserReference = mDatabase.child("users").child(mCurrentUserId);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                mCurrentUser = dataSnapshot.getValue(User.class);
                getCurrentUserVacation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "load User profile:onCancelled", databaseError.toException());
            }
        };
        currentUserReference.addValueEventListener(userListener);
    }

    private void setUpBottomNavigation() {
        mBinding.bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                bundle.putSerializable("current user id", mCurrentUserId);
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        fragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_likes:
                        fragment = new LikesFragment();
                        fragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_chat:
                        fragment = new ChatFragment();
                        fragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Bundle profileBundle = new Bundle();
                        profileBundle.putSerializable("current user", mCurrentUser);
                        profileBundle.putSerializable("vacation", mCurrentUserVacation);
                        fragment.setArguments(profileBundle);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                        break;
                }
                return true;
            }
        });
        mBinding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    public void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}