package com.example.eteacher.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eteacher.Model.SubmitWorkModel;
import com.example.eteacher.R;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityLecturePreviewViewerBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class LecturePreviewViewer extends AppCompatActivity {

    private static final int REQUEST_CODE = 1001;
    DatabaseReference mRef;
    FirebaseAuth _mAuth;
    String date, time;

    String classID, lectureID, pdfURL, title;
    ActivityLecturePreviewViewerBinding viewerBinding;
    private StorageReference storageReference;
    private Uri fileStorageUri;
    private DatabaseReference mClassworkRef, marksRef;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewerBinding = ActivityLecturePreviewViewerBinding.inflate(getLayoutInflater());
        setContentView(viewerBinding.getRoot());
        getIntentValues();
        getmarks();
        getLecture();

        downloadPDF();
        getCurrentDate();
        btnSubmiAssignmentQuiz();
    }

    private void getmarks() {
        marksRef.child("Marks").child(classID).child(_mAuth.getCurrentUser().getUid()).child(lectureID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    viewerBinding.obtainMarks.setText(snapshot.child("marks").getValue(String.class));
                } else {
                    viewerBinding.obtainMarks.setText(R.string.not_marked);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void btnSubmiAssignmentQuiz() {
        viewerBinding.btnSubmit.setOnClickListener(v -> {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            getFileAttachment();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            ActionUtils.ShowSnackBar(viewerBinding.RLParent,
                                    "Warning! Storage Permission Required",
                                    LecturePreviewViewer.this);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                       PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });
    }

    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat Tf = new SimpleDateFormat("hh:mm:a", Locale.getDefault());

        date = df.format(c);
        time = Tf.format(c);
    }


    private void getFileAttachment() {

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
                mProgressDialog.show();
                fileStorageUri = data.getData();
                storageReference.child("ClassWork").putFile(fileStorageUri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uri.isComplete()) ;
                    Uri pdfURI = uri.getResult();


                    String pushID = mClassworkRef.push().getKey();
                    SubmitWorkModel submitWorkModel = new SubmitWorkModel(_mAuth.getCurrentUser().getUid(), classID, lectureID, time, date, pdfURI.toString());

                    mClassworkRef.child("Student Work").child(classID).child(lectureID).child(pushID).setValue(submitWorkModel)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    mProgressDialog.dismiss();
                                    ActionUtils.ShowSnackBar(viewerBinding.RLParent, "Submitted", LecturePreviewViewer.this);
                                    startActivity(new Intent(LecturePreviewViewer.this, ClassroomlistActivity.class));
                                    finish();
                                } else {
                                    mProgressDialog.dismiss();
                                    ActionUtils.ShowSnackBar(viewerBinding.RLParent, "Can't Uploaded", LecturePreviewViewer.this);
                                }
                            });
                });
            } else {
                ActionUtils.ShowSnackBar(viewerBinding.RLMain, "Warning: Please Select file", LecturePreviewViewer.this);
            }
        }
    }

    private void downloadPDF() {
        viewerBinding.btnDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Dexter.withContext(LecturePreviewViewer.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                download();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                ActionUtils.ShowSnackBar(viewerBinding.RLMain, "Permission Required", LecturePreviewViewer.this);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private void download() {
        Uri pdfUri = Uri.parse(pdfURL);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        try {
            if (downloadManager != null) {

                File direct = new File(Environment.getExternalStorageDirectory()
                        + "/E Teacher");

                if (!direct.exists()) {
                    direct.mkdirs();
                }

                DownloadManager.Request managerRequest = new DownloadManager.Request(pdfUri);
                managerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle(title)
                        .setDescription("Downloading..." + title)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, title)
                        .setMimeType(getMimType(pdfUri));

                downloadManager.enqueue(managerRequest);
                ActionUtils.ShowSnackBar(viewerBinding.RLMain, "Download Started", LecturePreviewViewer.this);

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, pdfUri);
                startActivity(intent);
            }
        } catch (Exception e) {
            Timber.e("E:" + e.getMessage());
        }
    }


    private void getLecture() {
        mRef = FirebaseDatabase.getInstance().getReference();

        mRef.child("Classwork").child(classID).child(lectureID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            title = snapshot.child("title").getValue(String.class);

                            String desc = snapshot.child("desc").getValue(String.class);
                            String dueDate = snapshot.child("dueDate").getValue(String.class);

                            String topic = snapshot.child("tpoic").getValue(String.class);
                            String type = snapshot.child("type").getValue(String.class);

                            String marks = snapshot.child("marks").getValue(String.class);

                            pdfURL = snapshot.child("pdfURl").getValue(String.class);

                            viewerBinding.title.setText(title);
                            viewerBinding.description.setText(desc);

                            viewerBinding.duedate.setText(dueDate);
                            viewerBinding.topic.setText(topic);

                            viewerBinding.marks.setText("Marks: " + marks);
                            viewerBinding.totalMarks.setText("Marks: "+ marks);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getIntentValues() {
        lectureID = getIntent().getStringExtra("pushID");
        classID = getIntent().getStringExtra("classID");

        storageReference = FirebaseStorage.getInstance().getReference();

        mClassworkRef = FirebaseDatabase.getInstance().getReference();
        marksRef = FirebaseDatabase.getInstance().getReference();

        _mAuth = FirebaseAuth.getInstance();


        mProgressDialog = new ProgressDialog(LecturePreviewViewer.this);
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setMessage("Submitting Class Work");

    }

    public String getMimType(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}