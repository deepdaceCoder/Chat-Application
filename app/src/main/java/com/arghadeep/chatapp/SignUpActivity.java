package com.arghadeep.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameView = (EditText)findViewById(R.id.userName);
        emailView = (EditText)findViewById(R.id.email);
        passwordView = (EditText)findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void onSignUp(View view) {
        final String username, email, password;
        username = usernameView.getText().toString();
        email = emailView.getText().toString();
        password = passwordView.getText().toString();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            Toast.makeText(this,"Please enter email and password",Toast.LENGTH_SHORT).show();
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        String name = mAuth.getCurrentUser().getUid();
                        DatabaseReference databaseReference = mDatabase.child(name);
                        databaseReference.child("Name").setValue(username);
                        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                    }
                }
            });
        }
    }

    public void onSignIn(View view) {
        startActivity(new Intent(this,SignInActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign,menu);
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

        return super.onOptionsItemSelected(item);
    }
}
