package com.sophiaxiang.wanderlust;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sophiaxiang.wanderlust.databinding.ActivityMainBinding;
import com.sophiaxiang.wanderlust.fragments.ChatFragment;
import com.sophiaxiang.wanderlust.fragments.FeedFragment;
import com.sophiaxiang.wanderlust.fragments.MyVacationFragment;
import com.sophiaxiang.wanderlust.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
                    case R.id.action_vacation:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, new MyVacationFragment()).commit();
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
}