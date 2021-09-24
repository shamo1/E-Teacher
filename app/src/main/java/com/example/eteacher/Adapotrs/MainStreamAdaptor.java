package com.example.eteacher.Adapotrs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eteacher.Activities.LecturePreviewUploader;
import com.example.eteacher.Activities.LecturePreviewViewer;
import com.example.eteacher.Model.ClassworkModel;
import com.example.eteacher.databinding.CustomMainstreamLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainStreamAdaptor extends RecyclerView.Adapter<MainStreamAdaptor.MyViewHolder> {

    Context context;
    ArrayList<ClassworkModel> classworkModels;
    FirebaseAuth _mAuth;

    CustomMainstreamLayoutBinding mainstreamLayoutBinding;

    public MainStreamAdaptor(Context context, ArrayList<ClassworkModel> classworkModels) {
        this.context = context;
        this.classworkModels = classworkModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mainstreamLayoutBinding = CustomMainstreamLayoutBinding.inflate(inflater, parent, false);
        return new MyViewHolder(mainstreamLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String title = classworkModels.get(position).getTitle();

        String desc = classworkModels.get(position).getDesc();
        String dueDate = classworkModels.get(position).getDueDate();

        String topic = classworkModels.get(position).getTpoic();
        String type = classworkModels.get(position).getType();

        String marks = classworkModels.get(position).getMarks();

        String userID = classworkModels.get(position).getUserID();
        String classID = classworkModels.get(position).getClassID();

        holder.bindView(title, desc, dueDate, topic, type, marks);
        _mAuth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(v -> {
            if (userID.equals(_mAuth.getCurrentUser().getUid())) {
                Intent intent = new Intent(context, LecturePreviewUploader.class);
                intent.putExtra("pushID", classworkModels.get(position).getPushID());
                intent.putExtra("classID", classID);

                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, LecturePreviewViewer.class);
                intent.putExtra("pushID", classworkModels.get(position).getPushID());
                intent.putExtra("classID", classID);

                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return classworkModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        CustomMainstreamLayoutBinding mainstreamLayoutBinding;

        public MyViewHolder(@NonNull CustomMainstreamLayoutBinding mainstreamLayoutBinding) {
            super(mainstreamLayoutBinding.getRoot());
            this.mainstreamLayoutBinding = mainstreamLayoutBinding;
        }

        @SuppressLint("SetTextI18n")
        public void bindView(String title, String desc, String dueDate, String topic, String type, String marks) {
            mainstreamLayoutBinding.title.setText(title);

            mainstreamLayoutBinding.topic.setText(topic);
            mainstreamLayoutBinding.description.setText(desc);

            mainstreamLayoutBinding.duedate.setText("Submit By: " + dueDate);
            mainstreamLayoutBinding.type.setText(type);

            mainstreamLayoutBinding.marks.setText("Marks: " + marks);
        }
    }
}
