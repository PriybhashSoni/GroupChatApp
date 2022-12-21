package com.pys.groupchatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pys.groupchatapplication.Models.User;

public class RegisterActivty extends AppCompatActivity {
    TextInputEditText textemail,textpasword,textName;
    ProgressBar progressBar;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activty);

        textemail=(TextInputEditText)findViewById(R.id.email_register);
        textpasword=(TextInputEditText)findViewById(R.id.password_register);
        textName=(TextInputEditText)findViewById(R.id.name_register);
        progressBar=(ProgressBar)findViewById(R.id.ProgressBarRegister);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void RegisterUser(View v){
        progressBar.setVisibility(View.VISIBLE);
        final String email=textemail.getText().toString();
        final String password=textpasword.getText().toString();
        final String name=textName.getText().toString();

        if(!email.equals("")&&!password.equals("")&&password.length()>6){
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //insert value in db
                                FirebaseUser firebaseUser=auth.getCurrentUser();
                                User u=new User();
                                u.setName(name);
                                u.setEmail(email);
                                reference.child(firebaseUser.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"User Register Successfully",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            finish();
                                            Intent i=new Intent(RegisterActivty.this,GroupChatActivity.class);
                                            startActivity(i);
                                        }
                                        else{
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(),"user could not be created ",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        }
                    });
        }
    }

    public  void go_to_login(View v){
        Intent i=new Intent(RegisterActivty.this,MainActivity.class);
        startActivity(i);
    }
}