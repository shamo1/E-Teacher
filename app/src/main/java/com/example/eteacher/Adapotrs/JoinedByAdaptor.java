package com.example.eteacher.Adapotrs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eteacher.Model.JoinedByModel;
import com.example.eteacher.databinding.CustomStudentListLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinedByAdaptor extends RecyclerView.Adapter<JoinedByAdaptor.MyViewHolder> {

    Context context;
    ArrayList<JoinedByModel> joinedByModels;

    CustomStudentListLayoutBinding studentListLayoutBinding;

    public JoinedByAdaptor() {
    }

    public JoinedByAdaptor(Context context, ArrayList<JoinedByModel> joinedByModels) {
        this.context = context;
        this.joinedByModels = joinedByModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        studentListLayoutBinding = CustomStudentListLayoutBinding.inflate(inflater, parent, false);

        return new MyViewHolder(studentListLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String joinDate = joinedByModels.get(position).getDate();
        String userID = joinedByModels.get(position).getUserID();
        String joinID = joinedByModels.get(position).getJoinID();
        String classID = joinedByModels.get(position).getClassID();

        holder.bindViews(joinDate, userID);
        holder.removeStudent(classID, joinID, userID);
    }

    @Override
    public int getItemCount() {
        return joinedByModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        CustomStudentListLayoutBinding studentListLayoutBinding;

        public MyViewHolder(@NonNull CustomStudentListLayoutBinding studentListLayoutBinding) {
            super(studentListLayoutBinding.getRoot());
            this.studentListLayoutBinding = studentListLayoutBinding;
        }

        void bindViews(String joinedDate, String studentID) {
            DatabaseReference mJoinedByRed = FirebaseDatabase.getInstance().getReference();
            mJoinedByRed.child("Users").child(studentID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        studentListLayoutBinding.studentName.setText(snapshot.child("name").getValue(String.class));
                        studentListLayoutBinding.joineDate.setText(joinedDate);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void removeStudent(String classID, String joinID, String studentID) {
            studentListLayoutBinding.btnRemove.setOnClickListener(v ->
            {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Are your sure to remove Student")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            DatabaseReference mremoveRef = FirebaseDatabase.getInstance().getReference();
                            mremoveRef.child("Joined By").child(classID).child(joinID).removeValue().addOnCompleteListener(task -> {
                                DatabaseReference mRemRef = FirebaseDatabase.getInstance().getReference();
                                mRemRef.child("Joined Classroom").child(studentID).child(joinID).removeValue();
                            });
                        }).setNegativeButton("Cnacle", (dialog, which) -> {

                }).show();

            });

        }
    }
}
