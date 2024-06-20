package com.example.firebaseprojectapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.firebaseprojectapp.R;
import com.example.firebaseprojectapp.adapter.UsersAdapter;
import com.example.firebaseprojectapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {
     RecyclerView  recUser;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    AppCompatButton  btnUpdate;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        recUser =findViewById(R.id.recUser);
        btnUpdate =findViewById(R.id.btnUpdate);
        progressDialog =new ProgressDialog(DashBoardActivity.this);
        progressDialog.setTitle("Loading Account");
        progressDialog.setMessage("We are loading all accounts");
        getSupportActionBar().setTitle("All User List");

        progressDialog.show();
        database =FirebaseDatabase.getInstance();
        auth =FirebaseAuth.getInstance();
        final UsersAdapter adapter =new UsersAdapter(list,DashBoardActivity.this);
        recUser.setAdapter(adapter);
        LinearLayoutManager layoutManager =new LinearLayoutManager(DashBoardActivity.this);
        recUser.setLayoutManager(layoutManager);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(DashBoardActivity.this,UpdateActivity.class);
                startActivity(intent);
            }
        });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users =dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());

                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                      progressDialog.dismiss();
                    }


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.settings:
                Intent i =new Intent(DashBoardActivity.this,UpdateActivity.class);
                startActivity(i);
//                Toast.makeText(this, "Setting Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                auth.signOut();
                Intent intent =new Intent(DashBoardActivity.this,LoginActivity.class);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}