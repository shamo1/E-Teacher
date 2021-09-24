package com.example.eteacher.Activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.Model.ClassroomModel;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityCreateClassroomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class CreateClassroomActivity extends AppCompatActivity {

    ActivityCreateClassroomBinding createClassroomBinding;

    DatabaseReference mClasssRef, publicClassRef;
    FirebaseAuth mAuth;

    String joinCode, pushID, teacherID,
            subjectName, section, department,
            time, semester, amPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createClassroomBinding = ActivityCreateClassroomBinding.inflate(getLayoutInflater());
        setContentView(createClassroomBinding.getRoot());

        setSupportActionBar(createClassroomBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        timePickerClicklistener();
        initVars();
    }

    private void timePickerClicklistener() {
        createClassroomBinding.timePicker.getEditText().setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            new TimePickerDialog(CreateClassroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (hourOfDay > 12) {
                        amPM = "PM";
                    } else if (hourOfDay < 12) {
                        amPM = "AM";
                    }
                    createClassroomBinding.timePicker.getEditText().setText(hourOfDay + ":" + minute + " " + amPM);
                }
            }, hour, minute, false).show();

        });
    }

    private void initVars() {
        mClasssRef = FirebaseDatabase.getInstance().getReference();
        publicClassRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        teacherID = mAuth.getCurrentUser().getUid();
    }

    public void creteClassroom(View view) {
        createClassroomBinding.progressbarcreateClass.setVisibility(View.VISIBLE);
        getUserInput();
        if (isValid()) {
            cretaeClassRoom();
        } else {
            createClassroomBinding.progressbarcreateClass.setVisibility(View.GONE);
        }
    }


    private boolean isValid() {
        if (joinCode.isEmpty()) {
            createClassroomBinding.edtJoincode.setError("Join Code Required");
            return false;
        } else if (subjectName.isEmpty()) {
            createClassroomBinding.edtSubject.setError("Subject Name Required");
            return false;
        } else {
            return true;
        }
    }

    private void cretaeClassRoom() {
        pushID = mClasssRef.push().getKey();

        ClassroomModel classroomModel = new ClassroomModel(pushID, joinCode, subjectName, section, semester, department,
                time, pushID, teacherID);

        mClasssRef.child("Private Classroom").child(teacherID).child(pushID).setValue(classroomModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        publicClassRef.child("Public Classroom").child(pushID).setValue(classroomModel).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                clearTextFields();

                                createClassroomBinding.progressbarcreateClass.setVisibility(View.GONE);
                                ActionUtils.ShowSnackBar(createClassroomBinding.createClassRoot,
                                        "Success! Classroom Created", CreateClassroomActivity.this);

                            } else {
                                clearTextFields();
                            }
                        });
                    }
                });

    }

    private void getUserInput() {

        joinCode = Objects.requireNonNull(createClassroomBinding.edtJoincode.getEditText()).getText().toString();
        subjectName = Objects.requireNonNull(createClassroomBinding.edtSubject.getEditText()).getText().toString();

        section = Objects.requireNonNull(createClassroomBinding.edtSection.getEditText()).getText().toString();
        semester = Objects.requireNonNull(createClassroomBinding.edtDepartment.getEditText()).getText().toString();

        department = createClassroomBinding.edtDepartment.getEditText().getText().toString();
        time = Objects.requireNonNull(createClassroomBinding.timePicker.getEditText()).getText().toString();

    }


    private void clearTextFields() {
        Objects.requireNonNull(createClassroomBinding.edtJoincode.getEditText()).setText("");
        Objects.requireNonNull(createClassroomBinding.edtSubject.getEditText()).setText("");

        Objects.requireNonNull(createClassroomBinding.edtSection.getEditText()).setText("");
        Objects.requireNonNull(createClassroomBinding.edtSemester.getEditText()).setText("");

        Objects.requireNonNull(createClassroomBinding.edtDepartment.getEditText()).setText("");
        Objects.requireNonNull(createClassroomBinding.timePicker.getEditText()).setText("");
    }
}