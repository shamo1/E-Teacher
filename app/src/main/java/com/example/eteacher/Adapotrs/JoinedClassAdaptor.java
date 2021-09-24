package com.example.eteacher.Adapotrs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eteacher.Activities.StudentDashboard;
import com.example.eteacher.Model.JoinedByModel;
import com.example.eteacher.databinding.CustomJoinclassLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinedClassAdaptor extends RecyclerView.Adapter<JoinedClassAdaptor.MyViewHolder> {

    Context context;
    ArrayList<JoinedByModel> joinclassModels;

    CustomJoinclassLayoutBinding joinclassLayoutBinding;
    DatabaseReference mRef;
    String subjectName, section, department, classTime;


    public JoinedClassAdaptor(Context context, ArrayList<JoinedByModel> joinclassModels) {
        this.context = context;
        this.joinclassModels = joinclassModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        joinclassLayoutBinding = CustomJoinclassLayoutBinding.inflate(inflater, parent, false);

        return new MyViewHolder(joinclassLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String joinID = joinclassModels.get(position).getJoinID();
        String classID = joinclassModels.get(position).getClassID();

        String userID = joinclassModels.get(position).getUserID();
        String joinDate = joinclassModels.get(position).getDate();
        Toast.makeText(context, userID + classID, Toast.LENGTH_SHORT).show();
        holder.bindView(classID, joinID, userID, joinDate);

        holder.itemView.setOnClickListener(v -> {
            Intent studentIntent = new Intent(context, StudentDashboard.class);
            studentIntent.putExtra("classID", classID);
            context.startActivity(studentIntent);
        });

        DatabaseReference mLiveRef = FirebaseDatabase.getInstance().getReference();

        mLiveRef.child("Live Classes").child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String isLive = snapshot.child("isLive").getValue(String.class);
                    if (!isLive.equals("true"))
                    {
                        holder.joinclassLayoutBinding.liveIcon.setVisibility(View.GONE);
                    }
                } else {
                    holder.joinclassLayoutBinding.liveIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return joinclassModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        CustomJoinclassLayoutBinding joinclassLayoutBinding;

        public MyViewHolder(CustomJoinclassLayoutBinding joinclassLayoutBinding) {
            super(joinclassLayoutBinding.getRoot());
            this.joinclassLayoutBinding = joinclassLayoutBinding;
        }

        public void bindView(String classID, String joinID, String userID, String date) {
            mRef = FirebaseDatabase.getInstance().getReference();

            mRef.child("Public Classroom").child(classID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            subjectName = snapshot.child("subjectName").getValue(String.class);
                            section = snapshot.child("section").getValue(String.class);

                            department = snapshot.child("department").getValue(String.class);
                            classTime = snapshot.child("classTime").getValue(String.class);

                            joinclassLayoutBinding.tvsubjectName.setText(subjectName);
                            joinclassLayoutBinding.tvSection.setText(section);
                            joinclassLayoutBinding.tvDepartment.setText(department);
                            joinclassLayoutBinding.classTime.setText(classTime);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }
    }
}
