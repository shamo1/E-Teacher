package com.example.eteacher.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.databinding.ActivityLiveCassBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class LiveCassActivity extends AppCompatActivity {

    ActivityLiveCassBinding liveCassBinding;
    String classID, liveJoinCode, subjectName;
    URL serverURL;
    private DatabaseReference mLiveRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveCassBinding = ActivityLiveCassBinding.inflate(getLayoutInflater());
        setContentView(liveCassBinding.getRoot());

        setSupportActionBar(liveCassBinding.toolbarLive);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initVals();
        btnClickListeners();
    }

    private void initVals() {

        classID = getIntent().getStringExtra("classID");
        subjectName = getIntent().getStringExtra("subjectName");

        // ! ->Setting text Fields
        liveCassBinding.tvClassID.setText(classID);
        liveCassBinding.tvdpSubjectName.setText(subjectName);
    }

    private void btnClickListeners() {
        liveCassBinding.btnStartVideo.setOnClickListener(v -> {
            getStrinValues();
            if (isValid()) {

                HashMap<String, String> livemap = new HashMap<>();
                livemap.put("isLive", "true");
                mLiveRef = FirebaseDatabase.getInstance().getReference();
                mLiveRef.child("Live Classes").child(classID).setValue(livemap);

                setJitSiConfiguration();
                JitsiMeetConferenceOptions jitsiMeetConferenceOptions =
                        new JitsiMeetConferenceOptions.Builder()
                                .setRoom(liveJoinCode)
                                .setWelcomePageEnabled(true)
                                .build();
                JitsiMeetActivity.launch(LiveCassActivity.this, jitsiMeetConferenceOptions);

            }
        });

        liveCassBinding.btnInvite.setOnClickListener(v -> {
            getStrinValues();
            if (isValid()) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, " Invitation to join Live Class");

                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Class ID" + classID + ", " + "Live Join Code" + liveJoinCode);
                startActivity(Intent.createChooser(intent, "Invite Student Using"));
            }
        });
    }

    private void setJitSiConfiguration() {
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(true)
                            .build();

            JitsiMeet.setDefaultConferenceOptions(defaultOptions);


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        if (liveJoinCode.isEmpty()) {
            liveCassBinding.edtvideoJoinCode.getEditText().setError("Error! Join code can't be empty");
            return false;
        } else if (liveJoinCode.length() <= 8) {
            liveCassBinding.edtvideoJoinCode.getEditText().setError("Warning! Join code can't less then 8 characters");
            return false;
        } else {
            return true;
        }
    }

    private void getStrinValues() {
        liveJoinCode = Objects.requireNonNull(liveCassBinding.edtvideoJoinCode.getEditText()).getText().toString();
    }
}