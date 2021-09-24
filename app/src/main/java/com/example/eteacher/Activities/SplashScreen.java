package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    ActivityMainBinding mainBinding;

    FirebaseAuth mAuth;
    FirebaseUser mCurrentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater()); // ! -> Enabling Viewbinding
        setContentView(mainBinding.getRoot());

        initVar();
        splashDelay();
    }

    private void initVar() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser();
    }

    //    ! -> Splash Screen Code  with Delay of 4000 MiliSeconds.....
    private void splashDelay() {
        int DELAY_TIME = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentuser != null && mCurrentuser.isEmailVerified()) {
                    startActivity(new Intent(SplashScreen.this, ClassroomlistActivity.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
                finish();
            }
        }, DELAY_TIME);
    }
}