package com.pys.groupchatapplication;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pys.groupchatapplication.Adapter.MessageAdapter;
import com.pys.groupchatapplication.Models.AllMethods;
import com.pys.groupchatapplication.Models.Message;
import com.pys.groupchatapplication.Models.User;
import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messagedb;
    DatabaseReference passwordDB;
    MessageAdapter messageAdapter;
    User u;
    List<Message> messages;

    RecyclerView rvMessage;
    EditText etMessage;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        init();
    }
    public void init(){
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        u=new User();

        rvMessage=(RecyclerView)findViewById(R.id.rvMessaage);
        etMessage=(EditText)findViewById(R.id.mssg1);
        imgButton=(ImageButton)findViewById(R.id.imgBtn);
        imgButton.setOnClickListener(this);
        messages=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
             if(!TextUtils.isEmpty(etMessage.getText().toString())){
               Message message=new Message(etMessage.getText().toString(),u.getName());
                 etMessage.setText("");
                 messagedb.push().setValue(message);
             }
             else{
                 Toast.makeText(getApplicationContext(),"you cant send empty mssg",Toast.LENGTH_SHORT);
             }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menuLogout){
            auth.signOut();
            finish();
            startActivity(new Intent(GroupChatActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart(){
        super.onStart();
        final FirebaseUser currentUser=auth.getCurrentUser();
        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());
        database.getReference("Users").child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        u=snapshot.getValue(User.class);
                        u.setUid(currentUser.getUid());
                        AllMethods.name=u.getName();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                           // to the error
                    }
                });
                messagedb=database.getReference("messages");
                messagedb.addChildEventListener(new ChildEventListener()
                {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                messages.add(message);
                displayMessage(messages);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                message.getMessage();
                List<Message> newMessages=new ArrayList<Message>();
                for (Message m:messages){
                    if(m.getKey().equals(message.getKey)){
                        newMessages.add(message);
                    }
                    else{
                        newMessages.add(m);
                    }
                }
                messages=newMessages;
                displayMessage(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
             Message message=snapshot.getValue(Message.class);
             message.setKey(snapshot.getKey());
             List<Message>  newmessages=new ArrayList<Message>();
             for (Message m:messages){
                 if(!m.getKey().equals(message.getKey())){
                          if(!m.getKey().equals(message.getKey())){
                              newmessages.add(m);
                          }
                 }
                 messages=newmessages;
                 displayMessage(messages);
             }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages=new ArrayList<>();
    }

    private void displayMessage(List<Message> messagess) {
        rvMessage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        messageAdapter=new MessageAdapter(GroupChatActivity.this,messagess,messagedb);
        rvMessage.setAdapter(messageAdapter);
    }
}