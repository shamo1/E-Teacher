package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.Model.JoinedByModel;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityJoinClassBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class JoinClassActivity extends AppCompatActivity {

    ActivityJoinClassBinding joinClassBinding;
    DatabaseReference mjoinRef, classRef, joinedByref;
    FirebaseAuth _mAuth;

    String classID, joinCode, date;
    JoinedByModel joinclassModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        joinClassBinding = ActivityJoinClassBinding.inflate(getLayoutInflater());
        setContentView(joinClassBinding.getRoot());

        setSupportActionBar(joinClassBinding.toolbarJoinClass);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initVars();

        btnClicklisteners();
        getCurrentDate();
    }

    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        date = df.format(c);
    }

    private void btnClicklisteners() {
        joinClassBinding.btnJoinClass.setOnClickListener(v -> {
            getStringvals();
            if (isvalid()) {
                joinClassBinding.progressbarJoinClass.setVisibility(View.VISIBLE);
                classRef.child("Public Classroom").child(classID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            joinNewClass(classID);
                        } else {
                            joinClassBinding.progressbarJoinClass.setVisibility(View.GONE);
                            ActionUtils.ShowSnackBar(joinClassBinding.joinClassParent, "Error! Invalid class id or Join Code", JoinClassActivity.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    //     ! -> Join Class
    private void joinNewClass(String classID) {

        String joinID = mjoinRef.push().getKey();
        joinclassModel = new JoinedByModel(classID, date, joinID,_mAuth.getCurrentUser().getUid());

        mjoinRef.child("Joined Classroom").child(_mAuth.getCurrentUser().getUid())
                .child(joinID).setValue(joinclassModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                joinedByref.child("Joined By").child(classID).child(joinID).setValue(joinclassModel)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                joinClassBinding.progressbarJoinClass.setVisibility(View.GONE);
                                ActionUtils.ShowSnackBar(joinClassBinding.joinClassParent, "Success! Classroom joined", JoinClassActivity.this);
                                backtoclassList();
                            }
                        });
            }
        });
    }

    private void backtoclassList() {
        new Handler().postDelayed(() -> startActivity(new Intent(JoinClassActivity.this, ClassroomlistActivity.class)), 500);
    }

    private boolean isvalid() {
        if (classID.isEmpty()) {
            joinClassBinding.edtClassID.getEditText().setError("Class Id Required");
            return false;
        } else if (joinCode.isEmpty()) {
            joinClassBinding.edtjoinclass.getEditText().setError("Join Code Required");
            return false;
        } else {
            return true;
        }
    }

    //    ! -> Initializing variables
    private void initVars() {
        mjoinRef = FirebaseDatabase.getInstance().getReference();
        classRef = FirebaseDatabase.getInstance().getReference();
        joinedByref = FirebaseDatabase.getInstance().getReference();
        _mAuth = FirebaseAuth.getInstance();
    }

    public void getStringvals() {
        classID = joinClassBinding.edtClassID.getEditText().getText().toString();
        joinCode = joinClassBinding.edtjoinclass.getEditText().getText().toString();
    }
}