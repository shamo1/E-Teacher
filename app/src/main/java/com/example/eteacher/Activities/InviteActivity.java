package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.databinding.ActivityInviteBinding;

import java.util.Objects;

public class InviteActivity extends AppCompatActivity {

    ActivityInviteBinding inviteBinding;
    String subjectName, classCode, joinCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inviteBinding = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(inviteBinding.getRoot());


        setSupportActionBar(inviteBinding.toolbarInvite);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getintentValues();
        setValues();
        btnClickListener();
    }

    private void btnClickListener() {
        inviteBinding.btnInvite.setOnClickListener(v -> {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);

            String sbName = "Subject Name: " + subjectName;
            String cID = "Class ID: " + classCode;
            String joinID = "Join ID: " + joinCode;


            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subjectName + " Invitation to join");
            intent.putExtra(android.content.Intent.EXTRA_TEXT,  sbName + " | " +  cID + " | "  + joinID);

            startActivity(Intent.createChooser(intent, "Invite Student Using"));
        });
    }

    private void setValues() {
        inviteBinding.tvdpSubjectName.setText(subjectName);
        inviteBinding.tvClassID.setText(classCode);
        inviteBinding.tvJoinCode.setText(joinCode);
    }

    private void getintentValues() {
        subjectName = getIntent().getStringExtra("subjectName");
        classCode = getIntent().getStringExtra("ClassID");
        joinCode = getIntent().getStringExtra("joinCode");
    }


}