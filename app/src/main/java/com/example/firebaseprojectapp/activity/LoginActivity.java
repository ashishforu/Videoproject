package com.example.firebaseprojectapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseprojectapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText  loginEmailAddress,loginPassword;
    AppCompatButton  btnLogin;
    TextView signUp;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    boolean isAllFieldsChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        loginEmailAddress=findViewById(R.id.loginEmailAddress);
        loginPassword=findViewById(R.id.loginPassword);
        btnLogin=findViewById(R.id.btnLogin);
        signUp=findViewById(R.id.signUp);
        auth =FirebaseAuth.getInstance();

        database =FirebaseDatabase.getInstance();

        progressDialog =new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle(" Login");
        progressDialog.setMessage("Login to your account");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

         btnLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String useremail=loginEmailAddress.getText().toString().trim();
                 String pwd=loginPassword.getText().toString().trim();

                 isAllFieldsChecked = CheckAllFields();

               if(isAllFieldsChecked){
                     progressDialog.show();
                     auth.signInWithEmailAndPassword(loginEmailAddress.getText().toString(),loginPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             progressDialog.dismiss();
                             if(task.isSuccessful()){
                                 Intent intent =new Intent(LoginActivity.this,DashBoardActivity.class);
                                 startActivity(intent);
                             }
                             else {
                                 Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                             }
                         }
                     });
                 }


             }
         });
    }

    private boolean CheckAllFields() {
        if (loginEmailAddress.length() == 0) {
            loginEmailAddress.setError("Email is required");
            return false;
        }


        if (loginPassword.length() == 0) {
            loginPassword.setError("Password is required");
            return false;
        } else if (loginPassword.length() < 6) {
            loginPassword.setError("Password must be minimum 8 characters");
            return false;
        }

        // after all validation return true.
        return true;
    }
}