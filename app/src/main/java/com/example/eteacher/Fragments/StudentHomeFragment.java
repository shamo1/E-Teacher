package com.example.eteacher.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eteacher.Adapotrs.MainStreamAdaptor;
import com.example.eteacher.Model.ClassworkModel;
import com.example.eteacher.databinding.FragmentStudentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentHomeFragment extends Fragment {

    FragmentStudentHomeBinding studentHomeBinding;
    String classID, subjectName;
    DatabaseReference mRef, mainStreamRef;
    FirebaseAuth _mAuth;

    ArrayList<ClassworkModel> classworkModels;
    MainStreamAdaptor mainStreamAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleArguments();
        initVars();
        getClassDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentHomeBinding = FragmentStudentHomeBinding.inflate(inflater, container, false);
        LinearLayoutManager LLmanager = new LinearLayoutManager(getContext());
        LLmanager.setReverseLayout(true);
        studentHomeBinding.recyclerviewMainStream.setLayoutManager(LLmanager);
        getMainStream();
        return studentHomeBinding.getRoot();
    }

    private void getClassDetails() {
        mRef.child("Public Classroom")
                .child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String subjectName = snapshot.child("subjectName").getValue(String.class);
                    String section = snapshot.child("section").getValue(String.class);

                    String department = snapshot.child("department").getValue(String.class);
                    String time = snapshot.child("classTime").getValue(String.class);

                    String classID = snapshot.child("classID").getValue(String.class);
                    String joinCode = snapshot.child("joinCode").getValue(String.class);

                    studentHomeBinding.tvsubjectName.setText(subjectName);
                    studentHomeBinding.tvSection.setText(section);

                    studentHomeBinding.tvDepartment.setText(department);
                    studentHomeBinding.time.setText(time);

                    studentHomeBinding.classID.setText(classID);
                    studentHomeBinding.joinCode.setText(joinCode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initVars() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mainStreamRef = FirebaseDatabase.getInstance().getReference();

        _mAuth = FirebaseAuth.getInstance();
        classworkModels = new ArrayList<>();
    }

    private void getBundleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            classID = bundle.getString("classID");
            subjectName = bundle.getString("subjectName");
        }
    }

    private void getMainStream() {
        mainStreamRef.child("Classwork").child(classID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ClassworkModel classworkModel = ds.getValue(ClassworkModel.class);
                        classworkModels.add(classworkModel);
                    }
                    mainStreamAdaptor = new MainStreamAdaptor(getContext(), classworkModels);

                    mainStreamAdaptor.notifyDataSetChanged();
                    studentHomeBinding.recyclerviewMainStream.setAdapter(mainStreamAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}