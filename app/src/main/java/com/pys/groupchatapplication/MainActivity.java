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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    TextInputEditText textemail,textpasword;
    ProgressBar progressBar;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Intent i=new Intent(MainActivity.this,GroupChatActivity.class);
            startActivity(i);
        }
        else{
            setContentView(R.layout.activity_main);
            textemail=(TextInputEditText)findViewById(R.id.email_login);
            textpasword=(TextInputEditText)findViewById(R.id.password_login);
            progressBar=(ProgressBar)findViewById(R.id.ProgressBarLogin);
            reference= FirebaseDatabase.getInstance().getReference().child("Users");

        }
    }
    public void LoginUser(View view){
        progressBar.setVisibility(View.VISIBLE);
        String email=textemail.getText().toString();
        String password=textpasword.getText().toString();

        if(!email.equals("") && password.equals("")){
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Log in ",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(MainActivity.this,GroupChatActivity.class);
                                startActivity(i);

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong Try Again in ",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    });
        }
    }
    public void go_to_register(View view){
        Intent intent=new Intent(MainActivity.this,RegisterActivty.class);
        startActivity(intent);
    }
    public void forgotPassword(View view){

    }
}