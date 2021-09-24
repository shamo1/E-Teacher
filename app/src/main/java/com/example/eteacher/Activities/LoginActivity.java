package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //    ! -> Declare Variables
    ActivityLoginBinding loginBinding;
    FirebaseAuth mAuth;

    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater()); // ! -> Enabling Viewbinding
        setContentView(loginBinding.getRoot());

        initVars();
    }

    private void initVars() {
        mAuth = FirebaseAuth.getInstance();
    }


    public void createOne(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private boolean isValid() {
        if (email.isEmpty()) {

            loginBinding.edtLoginEmail.setError("Email Required");
            return false;

        } else if (password.isEmpty()) {

            loginBinding.edtloginPassword.setError("Password Required");
            return false;

        } else {
            return true;
        }
    }

    private void getUserInputs() {
        email = Objects.requireNonNull(loginBinding.edtLoginEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(loginBinding.edtloginPassword.getEditText()).getText().toString();
    }

    public void Login(View view) {
        loginBinding.progressbarLogin.setVisibility(View.VISIBLE);
        getUserInputs();
        if (isValid()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser().isEmailVerified()) {

                        loginBinding.progressbarLogin.setVisibility(View.GONE);
                        ActionUtils.ShowSnackBar(loginBinding.loginRoot, "Success! Login successfull", LoginActivity.this);
                        new Handler().postDelayed(() -> startActivity(new Intent(LoginActivity.this, ClassroomlistActivity.class)), 1000);

                    } else {
                        loginBinding.progressbarLogin.setVisibility(View.GONE);
                        mAuth.signOut();
                        ActionUtils.ShowSnackBar(loginBinding.loginRoot, "Failed! Please verify your email", LoginActivity.this);
                    }
                } else {
                    loginBinding.progressbarLogin.setVisibility(View.GONE);
                    ActionUtils.ShowSnackBar(loginBinding.loginRoot, "Failed! Login Failed check Email or Password", LoginActivity.this);
                }
            });
        } else {
            loginBinding.progressbarLogin.setVisibility(View.GONE);
        }
    }
}