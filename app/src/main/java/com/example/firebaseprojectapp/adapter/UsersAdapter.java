package com.example.firebaseprojectapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseprojectapp.R;
import com.example.firebaseprojectapp.activity.DashBoardActivity;
import com.example.firebaseprojectapp.activity.LoginActivity;
import com.example.firebaseprojectapp.activity.UpdateActivity;
import com.example.firebaseprojectapp.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder>  {

    ArrayList<Users> list;
    Context context;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user_layout,parent,false);
        auth =FirebaseAuth.getInstance();
        progressDialog =new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Users users =list.get(position);
        Picasso.get() .load(users.getProfilepic()).placeholder(R.drawable.man).into(holder.image);
        holder.userNameList.setText(users.getUserName());
        holder.email.setText(users.getMail());
        holder.txtDob.setText(users.getDob());

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){

                            for (DataSnapshot snapshot1:snapshot.getChildren()){

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("We are loading "+users.getUserName() + " details");
                progressDialog.show();
                auth.signInWithEmailAndPassword(users.getMail(),users.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    //    progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent =new Intent(context,UpdateActivity.class);
                            context.startActivity(intent);
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
//                Intent intent =new Intent(context, UpdateActivity.class);
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView email, txtDob,userNameList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            email = itemView.findViewById(R.id.email);
            txtDob = itemView.findViewById(R.id.txtDob);
            userNameList = itemView.findViewById(R.id.userNameList);


        }
    }
}
