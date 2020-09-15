package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginResultActivity extends AppCompatActivity {
     TextView textView;
     Button backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_result);

        textView = findViewById(R.id.textView);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginResultActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        String email = bundle.getString("email");
        String userpw = bundle.getString("userpw");

        textView.setText(email +"/"+userpw);
    }
}