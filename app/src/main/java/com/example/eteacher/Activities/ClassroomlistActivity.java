package com.example.eteacher.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eteacher.Adapotrs.JoinedClassAdaptor;
import com.example.eteacher.Adapotrs.MyClassAdaptor;
import com.example.eteacher.Model.ClassroomModel;
import com.example.eteacher.Model.JoinedByModel;
import com.example.eteacher.R;
import com.example.eteacher.WidgetsUtils.ActionUtils;
import com.example.eteacher.databinding.ActivityClassroomlistBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ClassroomlistActivity extends AppCompatActivity {
    ActivityClassroomlistBinding classroomlistBinding;
    BottomSheetDialog bottomSheetDialog;


    DatabaseReference mDb, joinedClassRef;
    FirebaseAuth mAuth;

    String userID;

    ArrayList<ClassroomModel> classroomModels;
    ArrayList<JoinedByModel> joinclassModels;

    JoinedClassAdaptor joinedClassAdaptor;
    MyClassAdaptor myClassAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classroomlistBinding = ActivityClassroomlistBinding.inflate(getLayoutInflater());
        setContentView(classroomlistBinding.getRoot());

        initvars();
        getMyClassroomData();
        getJoinedClassRoomData();
        buttonClickListeners();
    }
    private void getJoinedClassRoomData() {
        classroomlistBinding.joinClassProg.setVisibility(View.VISIBLE);
        joinedClassRef.child("Joined Classroom").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                JoinedByModel joinclassModel = ds.getValue(JoinedByModel.class);
                                joinclassModels.add(joinclassModel);
                            }
                            classroomlistBinding.RLjoinclass.setVisibility(View.VISIBLE);
                            classroomlistBinding.joinClassProg.setVisibility(View.GONE);
                            joinedClassAdaptor = new JoinedClassAdaptor(ClassroomlistActivity.this, joinclassModels);

                            joinedClassAdaptor.notifyDataSetChanged();
                            classroomlistBinding.recyclerviewJoinedClasses.setAdapter(joinedClassAdaptor);
                        } else {
                            classroomlistBinding.RLjoinclass.setVisibility(View.VISIBLE);
                            classroomlistBinding.joinClassProg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getMyClassroomData() {

        classroomlistBinding.myClassprog.setVisibility(View.VISIBLE);
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        mDb.child("Private Classroom").child(userID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ClassroomModel classroomModel = ds.getValue(ClassroomModel.class);
                        classroomModels.add(classroomModel);
                    }

                    classroomlistBinding.myClassprog.setVisibility(View.GONE);
                    myClassAdaptor = new MyClassAdaptor(ClassroomlistActivity.this, classroomModels);

                    myClassAdaptor.notifyDataSetChanged();
                    classroomlistBinding.recyclerviewMyclass.setAdapter(myClassAdaptor);

                } else {
                    classroomlistBinding.myClassprog.setVisibility(View.GONE);
                    classroomlistBinding.llMyclassroom.setVisibility(View.GONE);
                    ActionUtils.ShowSnackBar(classroomlistBinding.classroomParent, "You Don't have Created any class", ClassroomlistActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void initvars() {
        mDb = FirebaseDatabase.getInstance().getReference();
        joinedClassRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        joinclassModels = new ArrayList<>();

        classroomlistBinding.recyclerviewMyclass.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        classroomlistBinding.recyclerviewJoinedClasses.setLayoutManager(new LinearLayoutManager(this));

        classroomModels = new ArrayList<>();
    }

    private void buttonClickListeners() {
        classroomlistBinding.createJoinButton.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(ClassroomlistActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.custom_create_join_bottomsheet, null);

            // ! -> Bottomsheet button clicklisteners
            bottomsheetButtonClicklistener(mView);

            bottomSheetDialog.setContentView(mView);
            bottomSheetDialog.show();
        });
    }

    private void bottomsheetButtonClicklistener(View mView) {
        LinearLayout btnCreateClass = mView.findViewById(R.id.btnCreateClass);
        LinearLayout btnJoinclass = mView.findViewById(R.id.btnJoinClass);

        btnCreateClass.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();
            startActivity(new Intent(ClassroomlistActivity.this, CreateClassroomActivity.class));
        });

        btnJoinclass.setOnClickListener(v -> {
            startActivity(new Intent(ClassroomlistActivity.this, JoinClassActivity.class));
        });
    }
}