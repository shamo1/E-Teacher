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
import com.example.eteacher.databinding.FragmentStudentAssignmentsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentAssignmentsFragment extends Fragment {


    FragmentStudentAssignmentsBinding studentAssignmentsBinding;
    String classID;
    private DatabaseReference mainStreamRef;
    ArrayList<ClassworkModel> classworkModels;
    MainStreamAdaptor mainStreamAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        Bundle bundle = getArguments();
        if (bundle != null) {
            classID = bundle.getString("classID");
        }

    }

    private void initVars() {
        mainStreamRef = FirebaseDatabase.getInstance().getReference();
        classworkModels = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentAssignmentsBinding = FragmentStudentAssignmentsBinding.inflate(inflater, container, false);
        studentAssignmentsBinding.recyclerviewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));

        getAssignmentsList();
        return studentAssignmentsBinding.getRoot();
    }

    private void getAssignmentsList() {
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
                    studentAssignmentsBinding.recyclerviewAssignments.setAdapter(mainStreamAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}