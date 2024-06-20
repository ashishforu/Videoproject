package com.example.firebaseprojectapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseprojectapp.R;
import com.example.firebaseprojectapp.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateActivity extends AppCompatActivity {
    TextView alreadyacc,DOB,uploadImage;
    EditText PersonName,EmailAddress,Password;
    AppCompatButton btnUpdatedata;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    CircleImageView  profile_image;
    Uri sFile ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setTitle("Update Details");
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database =FirebaseDatabase.getInstance();
        PersonName =findViewById(R.id.editTextTextPersonName);
        EmailAddress =findViewById(R.id.editTextTextEmailAddress);
        Password =findViewById(R.id.editTextTextPassword);
        DOB =findViewById(R.id.editTextDOB);
        btnUpdatedata =findViewById(R.id.btnUpdatedata);
        profile_image =findViewById(R.id.profile_image);
        uploadImage =findViewById(R.id.uploadImage);
        progressDialog =new ProgressDialog(UpdateActivity.this);
        progressDialog.setTitle("Updating Details");
        progressDialog.setMessage("We are Updating your account");


        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDOB();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });






        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfilepic())
                                .placeholder(R.drawable.man)
                                .into(profile_image);

                        PersonName.setText(users.getUserName());
                        EmailAddress.setText(users.getMail());
                        DOB.setText(users.getDob());
                        Password.setText(users.getPassword());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        btnUpdatedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name =PersonName.getText().toString();
                String email =EmailAddress.getText().toString();
                String dob =DOB.getText().toString();
                String pwd =Password.getText().toString();

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", name);
                obj.put("mail", email);
                obj.put("dob", dob);
                obj.put("password", pwd);
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                if (sFile==null){
                    progressDialog.dismiss();
                    Intent intent =new Intent(UpdateActivity.this,DashBoardActivity.class);
                    startActivity(intent);
                }else {
                    UploadImage();
                }

                Toast.makeText(UpdateActivity.this, "User Details Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private  void SelectImage(){
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");  //  */*
        startActivityForResult(intent,33);
    }


    private void selectDOB(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateActivity.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth){
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                String dateString = format.format(calendar.getTime());
                DOB.setText(dateString);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() !=null){
            sFile = data.getData();
            profile_image.setImageURI(sFile);

        }
    }


    private  void UploadImage(){

        final StorageReference reference = storage.getReference().child("profile_pictures")
                .child(FirebaseAuth.getInstance().getUid());

        reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                .child("profilepic").setValue(uri.toString());
                        Intent intent =new Intent(UpdateActivity.this,DashBoardActivity.class);
                        startActivity(intent);
                        Toast.makeText(UpdateActivity.this, "Profile picture Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}