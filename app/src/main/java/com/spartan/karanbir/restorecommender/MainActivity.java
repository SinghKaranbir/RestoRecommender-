package com.spartan.karanbir.restorecommender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLoginButton;
    private Button mSignUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginButton = (Button) findViewById(R.id.login);
        mSignUpButton = (Button) findViewById(R.id.signup);
        mSignUpButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.login:
                i = new Intent(this,LoginActivity.class);
                startActivity(i);
                break;
            case R.id.signup:
                i = new Intent(this, SignUpActivity.class);
                startActivity(i);
                break;
        }
    }
}
