package com.example.eteacher.Adapotrs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eteacher.Activities.Dashboard;
import com.example.eteacher.Activities.InviteActivity;
import com.example.eteacher.Model.ClassroomModel;
import com.example.eteacher.R;
import com.example.eteacher.databinding.CustomClassLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MyClassAdaptor extends RecyclerView.Adapter<MyClassAdaptor.MyViewHolder> {
    @NonNull
    @NotNull

    Context context;
    ArrayList<ClassroomModel> classroomModels;

    DatabaseReference mClassRoomRef;
    FirebaseAuth mAuth;

    long numberofStudents;
    String currentUserID;


    public MyClassAdaptor(@NotNull Context context, ArrayList<ClassroomModel> classroomModels) {
        this.context = context;
        this.classroomModels = classroomModels;
    }

    @Override
    public @NotNull MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CustomClassLayoutBinding customClassLayoutBinding;
        customClassLayoutBinding = CustomClassLayoutBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(customClassLayoutBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull MyClassAdaptor.MyViewHolder holder, int position) {
        String subjectNmae = classroomModels.get(position).getSubjectName();
        String section = classroomModels.get(position).getSection();
        String department = classroomModels.get(position).getDepartment();

        String classCode = classroomModels.get(position).getClassID();
        String joinCode = classroomModels.get(position).getJoinCode();


        holder.bindView(subjectNmae, section, department, classCode);

        menuButtonCLicked(holder.customClassLayoutBinding.btnMenu, classCode, joinCode, subjectNmae, holder.customClassLayoutBinding.classroomParent);

//        !-> Recycleritem Click Listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Dashboard.class);

            intent.putExtra("classID", classroomModels.get(position).getClassID());
            intent.putExtra("subjectName", classroomModels.get(position).getSubjectName());
            context.startActivity(intent);
        });
    }

    //    ! -> Menu Button Click listener
    @SuppressLint("NonConstantResourceId")
    private void menuButtonCLicked(ImageView btnMenu, String classCode, String joinCode, String subjectName, RelativeLayout classroomParent) {
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, btnMenu);
            popupMenu.getMenuInflater()
                    .inflate(R.menu.drop_down_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.inviteUser:
                        openInviteActivity(classCode, joinCode, subjectName);
                        break;
                    case R.id.delete:
                        deleteClass(classCode, btnMenu, classroomParent);
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void deleteClass(String classCode, ImageView btnImage, RelativeLayout claasRoomParent) {
        mAuth = FirebaseAuth.getInstance();
        mClassRoomRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("Confirmation")
                .setMessage("After deleting this class you wont be able to get Access back.  Are you sure to remove classroom ")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mClassRoomRef.child("Private Classroom").child(currentUserID)
                            .child(classCode)
                            .removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mClassRoomRef.child("Public Classroom").child(classCode)
                                    .removeValue().addOnCompleteListener(task1 -> {
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                            });
                        }

                    });
                })
                .setNegativeButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    private void openInviteActivity(String classCode, String joinCode, String subjectName) {
        Intent intent = new Intent(context, InviteActivity.class);
        intent.putExtra("ClassID", classCode);

        intent.putExtra("joinCode", joinCode);
        intent.putExtra("subjectName", subjectName);

        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return classroomModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        CustomClassLayoutBinding customClassLayoutBinding;

        public MyViewHolder(CustomClassLayoutBinding customClassLayoutBinding) {
            super(customClassLayoutBinding.getRoot());
            this.customClassLayoutBinding = customClassLayoutBinding;
        }

        void bindView(String subject, String section, String department, String classID) {
            customClassLayoutBinding.tvsubjectName.setText(subject);
            customClassLayoutBinding.tvSection.setText(section);

            customClassLayoutBinding.tvDepartment.setText(department);
            DatabaseReference mStudentRef = FirebaseDatabase.getInstance().getReference();

            mStudentRef.child("Joined By").child(classID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    numberofStudents = snapshot.getChildrenCount();
                                }
                                customClassLayoutBinding.students.setText(String.valueOf(numberofStudents));
                            } else {
                                customClassLayoutBinding.students.setText(R.string.nostudent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }
}
