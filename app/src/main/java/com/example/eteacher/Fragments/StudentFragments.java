package com.example.eteacher.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eteacher.Adapotrs.JoinedByAdaptor;
import com.example.eteacher.Model.JoinedByModel;
import com.example.eteacher.databinding.FragmentStudentFragmentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StudentFragments extends Fragment {


    FragmentStudentFragmentsBinding studentFragmentsBinding;
    DatabaseReference mRef;
    FirebaseAuth mAuth;

    ArrayList<JoinedByModel> joinedByModels;
    JoinedByAdaptor joinedByAdaptor;

    String classID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            classID = bundle.getString("classID");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentFragmentsBinding = FragmentStudentFragmentsBinding.inflate(inflater, container, false);
        initValus();
        getStudentList();
        return studentFragmentsBinding.getRoot();
    }

    private void getStudentList() {
        studentFragmentsBinding.stidentRecyclerView.setVisibility(View.GONE);
        mRef.child("Joined By").child(classID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        JoinedByModel joinclassModel = ds.getValue(JoinedByModel.class);
                        joinedByModels.add(joinclassModel);
                    }
                    studentFragmentsBinding.stidentRecyclerView.setVisibility(View.VISIBLE);
                    joinedByAdaptor = new JoinedByAdaptor(getContext(), joinedByModels);

                    joinedByAdaptor.notifyDataSetChanged();
                    studentFragmentsBinding.stidentRecyclerView.setAdapter(joinedByAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initValus() {
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        studentFragmentsBinding.stidentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        joinedByModels = new ArrayList<>();

    }
}