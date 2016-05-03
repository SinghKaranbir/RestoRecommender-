package com.spartan.karanbir.restorecommender;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by karanbir on 4/27/16.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener{

    public static String EMAIL_KEY = "email";
    public static String PASSWORD_KEY = "password";

    private Validator validator;
    private Button mLoginButton;
    private TextView mSignUp;

    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC, message = "Must be 6 chars and Alphanumeric")
    private EditText mPassword;
    @NotEmpty
    @Email
    private EditText mEmail;
    private HashMap<String,String> hashMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        validator = new Validator(this);
        validator.setValidationListener(this);
        hashMap = new HashMap<>();
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(this);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);
        mSignUp = (TextView) findViewById(R.id.link_signup);
        mSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                validator.validate();
                break;
            case R.id.link_signup:
                Intent it = new Intent(this, SignUpActivity.class);
                startActivity(it);
                finish();
        }
    }

    @Override
    public void onValidationSucceeded() {
        EMAIL_KEY = mEmail.getText().toString();
        PASSWORD_KEY = mPassword.getText().toString();
        LoginTask task = new LoginTask();
        task.execute(new String[]{EMAIL_KEY, PASSWORD_KEY});
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
    private class LoginTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            Integer result = 0;
            try{
                URL url = new URL("http://ec2-54-186-33-120.us-west-2.compute.amazonaws.com:9200/cmpe239_person/userdetails/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Email", params[0]);
                urlConnection.setRequestProperty("Password", params[1]);
                urlConnection.setRequestMethod("GET");
                int status = urlConnection.getResponseCode();
                if(status == 200) {
                    result = 1;
                }
                else {
                    result = 0;
                }

            } catch (IOException e) {


            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {

            //TODO
            /*
                if(result == 1) {
                    Intent it = new Intent(this, dashboard.class);
                    startActivity(it);
                    finish();
                } else {

                }
             */
        }
    }
}