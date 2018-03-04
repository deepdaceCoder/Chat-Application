package com.arghadeep.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private EditText messageView;
    private RecyclerView mMessageList;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (EditText)findViewById(R.id.typeMessage);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()==null)
                    startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        };

        mMessageList = (RecyclerView)findViewById(R.id.messageRec);
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Message,MessageViewHolder> FBRA = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.activity_single_message,
                MessageViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.setContent(model.getContent());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTime(model.getTime());
            }
        };
        mMessageList.setAdapter(FBRA);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.action_exit) {
            this.finishAffinity();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        else if(id==R.id.action_log_out) {
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSend(View view) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        Date date = new Date();
        final String dateText = dateFormat.format(date)+"pm";

        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        final String message = messageView.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            final DatabaseReference newPost = mDatabase.push();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newPost.child("time").setValue(dateText);
                    newPost.child("content").setValue(message);
                    newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mMessageList.scrollToPosition(mMessageList.getAdapter().getItemCount());
        messageView.setText(null);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public MessageViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setContent(String content) {
            TextView messageView = (TextView)view.findViewById(R.id.messageText);
            messageView.setText(content);
        }

        public void setUsername(String username) {
            TextView usernameView = (TextView)view.findViewById(R.id.usernameText);
            usernameView.setText(username);
        }

        public void setTime(String time) {
            TextView timeView = (TextView)view.findViewById(R.id.timeText);
            timeView.setText(time);
        }

    }
}
