package com.example.eteacher.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eteacher.Fragments.AssignmentsFragment;
import com.example.eteacher.Fragments.HomeFragment;
import com.example.eteacher.Fragments.StudentFragments;
import com.example.eteacher.R;
import com.example.eteacher.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding dashboardBinding;
    String classID;
    String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboardBinding.getRoot());
        getIntentsvalues();
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putString("classID", classID);
            args.putString("subjectName", subjectName);
            homeFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
        }
        dashboardBinding.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private void getIntentsvalues() {
        classID = getIntent().getStringExtra("classID");
        subjectName = getIntent().getStringExtra("subjectName");
    }


    //    ! Bottom navigation
    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        Bundle hmArgs = new Bundle();
                        hmArgs.putString("classID", classID);
                        hmArgs.putString("subjectName", subjectName);
                        homeFragment.setArguments(hmArgs);
                        selectedFragment = homeFragment;
                        break;
                    case R.id.assignments:
                        AssignmentsFragment assignmentsFragment = new AssignmentsFragment();
                        Bundle args = new Bundle();
                        args.putString("classID", classID);
                        args.putString("subjectName", subjectName);
                        assignmentsFragment.setArguments(args);
                        selectedFragment = assignmentsFragment;
                        break;
                    case R.id.students:
                        StudentFragments studentFragments = new StudentFragments();
                        Bundle stuarg = new Bundle();
                        stuarg.putString("classID", classID);
                        stuarg.putString("subjectName", subjectName);
                        studentFragments.setArguments(stuarg);
                        selectedFragment = studentFragments;
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                return true;
            };
}