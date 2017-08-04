package com.expensemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;


public class FirstActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        if (auth.getCurrentUser() != null) {
            email = auth.getCurrentUser().getEmail();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("userid",email);
            startActivity(i);
            finish();
        }
        else{
            auth = FirebaseAuth.getInstance();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

    }
}