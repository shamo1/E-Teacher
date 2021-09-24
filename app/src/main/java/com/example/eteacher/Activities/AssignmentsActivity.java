package com.example.eteacher.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.Model.ClassworkModel;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityAssignmentsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Calendar;
import java.util.Objects;

public class AssignmentsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1001;
    ActivityAssignmentsBinding activityAssignmentsBinding;
    String type, classID, fileURL, title, desc, dueDate, topic, pushID, marks;

    DatabaseReference mClassworkRef;
    StorageReference storageReference;
    ProgressDialog mProgressDialog;

    FirebaseAuth _mAuth;

    Uri fileStorageUri;

    int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAssignmentsBinding = ActivityAssignmentsBinding.inflate(getLayoutInflater());
        setContentView(activityAssignmentsBinding.getRoot());

        initvars();
        getStringExtras();
        btnclicklisteners();
        datePicker();
        createProgressDialog();

        setSupportActionBar(activityAssignmentsBinding.toolbarAssignment);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activityAssignmentsBinding.toolbarAssignment.setTitle(type);
    }

    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setMessage("Uploading....");
    }

    private void initvars() {
        mClassworkRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    //    ! -> Pick Due Date
    private void datePicker() {
        activityAssignmentsBinding.edtDueDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // !-> Launch Date Picker Dialog
            @SuppressLint("SetTextI18n") DatePickerDialog dpd = new DatePickerDialog(AssignmentsActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        activityAssignmentsBinding.edtDueDate.setText(dayOfMonth + "-" + month + "-" + year);
                    }, mYear, mMonth, mDay);
            dpd.show();
        });
    }


    private void btnclicklisteners() {
        activityAssignmentsBinding.btnAttachFile.setOnClickListener(v -> {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            getFileAttachment();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            ActionUtils.ShowSnackBar(activityAssignmentsBinding.RLParent,
                                    "Warning! Storage Permission Required",
                                    AssignmentsActivity.this);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                       PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });


//        ! -> Upload Classwork
        activityAssignmentsBinding.btnUpload.setOnClickListener(v -> {
            mProgressDialog.show();
            getStringvalus();
            if (isValid()) {
                pushID = mClassworkRef.push().getKey();
                if (fileStorageUri == null || fileStorageUri.toString().isEmpty()) {
                    _mAuth = FirebaseAuth.getInstance();
                    ClassworkModel classworkModel = new ClassworkModel(title, marks, desc, "null", dueDate, topic, classID, pushID, type, _mAuth.getCurrentUser().getUid());

                    mClassworkRef.child("Classwork").child(classID).child(pushID).setValue(classworkModel)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    mProgressDialog.dismiss();
                                    ActionUtils.ShowSnackBar(activityAssignmentsBinding.RLParent, type + " Uploaded", AssignmentsActivity.this);
                                    startActivity(new Intent(AssignmentsActivity.this, ClassroomlistActivity.class));
                                    finish();
                                } else {
                                    mProgressDialog.dismiss();
                                    ActionUtils.ShowSnackBar(activityAssignmentsBinding.RLParent, type + "Can't Uploaded", AssignmentsActivity.this);
                                }
                            });
                } else {
                    storageReference.child("ClassWork").putFile(fileStorageUri).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri pdfURI = uri.getResult();
                        ClassworkModel classworkModel = new ClassworkModel(title, marks, desc, pdfURI.toString(), dueDate, topic, classID, pushID, type, _mAuth.getCurrentUser().getUid());
                        mClassworkRef.child("Classwork").child(classID).child(pushID).setValue(classworkModel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        ActionUtils.ShowSnackBar(activityAssignmentsBinding.RLParent, type + " Uploaded", AssignmentsActivity.this);
                                        startActivity(new Intent(AssignmentsActivity.this, ClassroomlistActivity.class));
                                        finish();
                                    } else {
                                        mProgressDialog.dismiss();
                                        ActionUtils.ShowSnackBar(activityAssignmentsBinding.RLParent, type + "Can't Uploaded", AssignmentsActivity.this);
                                    }
                                });
                    });

                }
            }
        });
    }

    private boolean isValid() {
        if (title.isEmpty()) {
            activityAssignmentsBinding.edtTitle.getEditText().setError("Title Required");
            return false;
        } else if (marks.isEmpty()) {
            activityAssignmentsBinding.edtmarks.setError("Marks Required");
            return false;
        } else if (dueDate.isEmpty()) {
            activityAssignmentsBinding.edtDueDate.setError("Due Date Required");
            return false;
        } else {
            return true;
        }
    }

    private void getStringvalus() {
        title = activityAssignmentsBinding.edtTitle.getEditText().getText().toString();
        desc = activityAssignmentsBinding.edtDescription.getEditText().getText().toString();

        topic = activityAssignmentsBinding.edtTopic.getText().toString();
        dueDate = activityAssignmentsBinding.edtDueDate.getText().toString();

        marks = activityAssignmentsBinding.edtmarks.getText().toString();

    }

    private void getFileAttachment() {
        _mAuth = FirebaseAuth.getInstance();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null && requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fileStorageUri = data.getData();
            }
        }
    }


    private void getStringExtras() {
        type = getIntent().getStringExtra("ass");
        classID = getIntent().getStringExtra("classID");

        activityAssignmentsBinding.tvType.setText(type);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AssignmentsActivity.this, ClassroomlistActivity.class));
        finish();
    }
}