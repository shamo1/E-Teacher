package com.example.eteacher.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eteacher.Activities.AssignmentsActivity;
import com.example.eteacher.Adapotrs.MainStreamAdaptor;
import com.example.eteacher.Model.ClassworkModel;
import com.example.eteacher.R;
import com.example.eteacher.databinding.FragmentAssignmentsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AssignmentsFragment extends Fragment {
    FragmentAssignmentsBinding assignmentsBinding;
    BottomSheetDialog bottomSheetDialog;
    String classID;

    ArrayList<ClassworkModel> classworkModels;
    MainStreamAdaptor mainStreamAdaptor;
    private DatabaseReference mainStreamRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();

    }

    private void initVars() {
        mainStreamRef = FirebaseDatabase.getInstance().getReference();
        classworkModels = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assignmentsBinding = FragmentAssignmentsBinding.inflate(inflater, container, false);
        assignmentsBinding.recyclerviewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        buttonClickListeners();
        getBundleArguments();
        getmainStream();
        return assignmentsBinding.getRoot();
    }

    private void getmainStream() {
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
                    assignmentsBinding.recyclerviewAssignments.setAdapter(mainStreamAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBundleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            classID = bundle.getString("classID");
            Toast.makeText(getContext(), classID, Toast.LENGTH_SHORT).show();
        }
    }

    private void buttonClickListeners() {
        assignmentsBinding.addAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog = new BottomSheetDialog(getContext());
                View mView = getLayoutInflater().inflate(R.layout.custom_bottomsheet_assignments, null);

                bottomSheetDialog.setContentView(mView);
                bottomSheetDialog.show();


                LinearLayout btnAssignments = mView.findViewById(R.id.btnAssignments);
                LinearLayout btnQuiz = mView.findViewById(R.id.btnQuiz);
                LinearLayout btnLecture = mView.findViewById(R.id.btnLecture);

                btnAssignments.setOnClickListener(v1 -> {
                    Intent intent = new Intent(getContext(), AssignmentsActivity.class);
                    intent.putExtra("ass", "Assignment");
                    intent.putExtra("classID", classID);
                    startActivity(intent);
                });

                btnQuiz.setOnClickListener(v12 -> {
                    Intent intent = new Intent(getContext(), AssignmentsActivity.class);
                    intent.putExtra("ass", "Quiz");
                    intent.putExtra("classID", classID);
                    startActivity(intent);
                });
            }
        });
    }
}