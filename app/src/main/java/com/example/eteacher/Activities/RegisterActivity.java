package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.Model.UsersModel;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRefRefrence;

    ActivityRegisterBinding registerBinding;
    private String name, email, password, conPassword,
            phone, userID;

    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());  // ! -> Enabling ViewBindings
        setContentView(registerBinding.getRoot());

        initVars();
    }

    //  ! -> Initializing Variables and creating hooks
    private void initVars() {

        mAuth = FirebaseAuth.getInstance();
        mRefRefrence = FirebaseDatabase.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void RegisterMe(View view) {

        registerBinding.progressbarReg.setVisibility(View.VISIBLE);
        getUserInputs();

        if (isValid()) {
            registerUser();
        } else {
            registerBinding.progressbarReg.setVisibility(View.GONE);
        }
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        registerBinding.progressbarReg.setVisibility(View.GONE);
                        mRefRefrence = mRefRefrence.child("Users");
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        UsersModel usersModel = new UsersModel(name, email, phone, userID);
                        registerBinding.progressbarReg.setVisibility(View.GONE);

                        ActionUtils.ShowSnackBar(registerBinding.registerParennt, "Success! Registeration Completed Verify you email",
                                RegisterActivity.this);

                        mRefRefrence.child(userID).setValue(usersModel).addOnCompleteListener(task2 -> new Handler().postDelayed(() -> {

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }, 1000));
                    }
                });
            }
        });
    }

    //    ! -> Getting users input
    private void getUserInputs() {
        name = Objects.requireNonNull(registerBinding.edtName.getEditText()).getText().toString();
        email = Objects.requireNonNull(registerBinding.edtRegEmail.getEditText()).getText().toString();

        password = Objects.requireNonNull(registerBinding.edtregPassword.getEditText()).getText().toString();
        conPassword = Objects.requireNonNull(registerBinding.edtregCpassword.getEditText()).getText().toString();

        phone = Objects.requireNonNull(registerBinding.edtPhone.getEditText()).getText().toString();
    }

    private boolean isValid() {
        if (name.isEmpty() || name.length() <= 4) {
            registerBinding.edtName.setError("Valid Name Required!");
            return false;
        } else if (email.isEmpty() || !email.contains("@")) {
            registerBinding.edtRegEmail.setError("Valid Email Required");
            return false;
        } else if (password.isEmpty()) {
            registerBinding.edtregPassword.setError("Password Required");
            return false;
        } else if (conPassword.isEmpty() || !password.equals(conPassword)) {
            registerBinding.edtregPassword.setError("Password and confirm password must be same");
            return false;
        } else if (phone.isEmpty()) {
            registerBinding.edtPhone.setError("Phone Number Required");
            return false;
        }
        return true;
    }

    public void Loginlink(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}