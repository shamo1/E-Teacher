package com.example.eteacher.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eteacher.Fragments.StuListFragment;
import com.example.eteacher.Fragments.StudentAssignmentsFragment;
import com.example.eteacher.Fragments.StudentHomeFragment;
import com.example.eteacher.R;
import com.example.eteacher.databinding.ActivityStudentDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentDashboard extends AppCompatActivity {

    ActivityStudentDashboardBinding studentDashboardBinding;
    String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentDashboardBinding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(studentDashboardBinding.getRoot());
        setSupportActionBar(studentDashboardBinding.toolbarDashboard);

        getIntentValues();
        if (savedInstanceState == null) {
            StudentHomeFragment homeFragment = new StudentHomeFragment();
            Bundle args = new Bundle();
            args.putString("classID", classID);
            homeFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
        }
        studentDashboardBinding.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private void getIntentValues() {
        classID = getIntent().getStringExtra("classID");
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        StudentHomeFragment homeFragment = new StudentHomeFragment();
                        Bundle hmArgs = new Bundle();
                        hmArgs.putString("classID", classID);
                        homeFragment.setArguments(hmArgs);
                        selectedFragment = homeFragment;
                        break;
                    case R.id.assignments:
                        StudentAssignmentsFragment assignmentsFragment = new StudentAssignmentsFragment();
                        Bundle args = new Bundle();
                        args.putString("classID", classID);
                        assignmentsFragment.setArguments(args);
                        selectedFragment = assignmentsFragment;
                        break;
                    case R.id.students:
                        StuListFragment studentFragments = new StuListFragment();
                        Bundle stuarg = new Bundle();
                        stuarg.putString("classID", classID);
                        studentFragments.setArguments(stuarg);
                        selectedFragment = studentFragments;
                        break;
                }
                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        selectedFragment).commit();
                return true;
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.joinLive) {
            Intent intent = new Intent(this, JoinLiveClass.class);
            intent.putExtra("classID", classID);
            startActivity(intent);
        }
        return true;
    }
}