package com.example.eteacher.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.databinding.ActivityJoinLiveClassBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinLiveClass extends AppCompatActivity {

    ActivityJoinLiveClassBinding liveClassBinding;
    String classID, isLive;

    DatabaseReference isClassLiveref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveClassBinding = ActivityJoinLiveClassBinding.inflate(getLayoutInflater());
        setContentView(liveClassBinding.getRoot());

        initVars();
        getIntentValues();
        setIDinField();
        isClassLive();
    }

    private void isClassLive() {
        isClassLiveref.child("Live Classes").child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isLive = snapshot.child("isLive").getValue(String.class);
                    if (!isLive.equals("true")) {
                        liveClassBinding.btnStartVideo.setEnabled(false);
                        liveClassBinding.btnInvite.setEnabled(false);
                    }
                } else {
                    liveClassBinding.btnStartVideo.setEnabled(false);
                    liveClassBinding.btnInvite.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initVars() {
        isClassLiveref = FirebaseDatabase.getInstance().getReference();
    }

    private void setIDinField() {
        liveClassBinding.tvClassID.setText(classID);
    }

    private void getIntentValues() {
        classID = getIntent().getStringExtra("classID");
    }
}