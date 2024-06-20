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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
       TextView  alreadyacc,DOB,uploadImage;
       EditText  PersonName,EmailAddress,Password;
       AppCompatButton  Register;
       FirebaseAuth auth;
       FirebaseDatabase database;
       ProgressDialog progressDialog;
       CircleImageView profile_image;
       FirebaseStorage storage;
       Uri sFile ;
       boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        PersonName =findViewById(R.id.editTextTextPersonName);
        EmailAddress =findViewById(R.id.editTextTextEmailAddress);
        Password =findViewById(R.id.editTextTextPassword);
        DOB =findViewById(R.id.editTextDOB);
        Register =findViewById(R.id.btnRegister);
        alreadyacc =findViewById(R.id.alreadyacc);
        uploadImage =findViewById(R.id.uploadImage);
        profile_image =findViewById(R.id.profile_image);
        auth = FirebaseAuth.getInstance();

        progressDialog =new ProgressDialog(RegistrationActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        alreadyacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDOB();
            }
        });

    }

    private  void RegisterUser(){
       String email,pwd,name,dob;
       email = EmailAddress.getText().toString();
        pwd = Password.getText().toString();
        dob = DOB.getText().toString();
        name = PersonName.getText().toString();


        isAllFieldsChecked = CheckAllFields();
        if (isAllFieldsChecked){
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Users user=new Users(name,email,pwd,dob);
                        database =FirebaseDatabase.getInstance();
                        storage = FirebaseStorage.getInstance();
                        String id =task.getResult().getUser().getUid();
                        database.getReference().child("Users").child(id).setValue(user);
                        UploadImage();
                        Toast.makeText(RegistrationActivity.this, "User Created successfully", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

    }

    private boolean CheckAllFields() {

        if (PersonName.getText().toString().length() == 0) {
            PersonName.setError("Name is required");
            return false;
        }
        if (EmailAddress.getText().toString().length() == 0) {
            EmailAddress.setError("Email is required");
            return false;
        }


        if (Password.length() == 0) {
            Password.setError("Password is required");
            return false;
        } else if (Password.length() < 6) {
            Password.setError("Password must be minimum 8 characters");
            return false;
        }
        if (DOB.getText().toString().length() == 0) {

            Toast.makeText(this, "Please Select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sFile==null){
            Toast.makeText(this, "Please Upload Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        // after all validation return true.
        return true;
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


        DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                        Intent intent =new Intent(RegistrationActivity.this,DashBoardActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegistrationActivity.this, "Profile picture Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}