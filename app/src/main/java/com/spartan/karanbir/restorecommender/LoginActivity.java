package com.spartan.karanbir.restorecommender;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.spartan.karanbir.restorecommender.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public static String AUTHORITY ="ec2-54-186-33-120.us-west-2.compute.amazonaws.com:9200";
    public static String CMPE = "cmpe239_person";
    public static String USERDETAILS = "userdetails";

    private Validator validator;
    private Button mLoginButton;
    private TextView mSignUp;

    @Password(min = 4, scheme = Password.Scheme.NUMERIC, message = "Must be 4 chars and Numeric")
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
                Log.d("CLicked", "");
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
        Log.d("Hii",EMAIL_KEY);
        Log.d("Hii",PASSWORD_KEY);
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
    private class LoginTask extends AsyncTask<String, Void, User> {
        private User user;
        private Uri uri;
        @Override
        protected User doInBackground(String... params) {

            Log.i("Hii",params[1]);
            Log.i("Hii",params[0]);
            HttpURLConnection urlConnection = null;
            Integer result = 0;
            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority(AUTHORITY)
                        .appendPath(CMPE)
                        .appendPath(USERDETAILS)
                        .appendPath(params[0]);
                uri = builder.build();
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("username", params[0]);
                urlConnection.setRequestProperty("password", params[1]);
                urlConnection.setRequestMethod("GET");
                int status = urlConnection.getResponseCode();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";

                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(webPage);
                Log.d("JSON", jsonObject.toString());
                JSONObject source = jsonObject.getJSONObject("_source");

                Log.d("password",source.getString("password"));
                if(source.getString("password").equals(params[1])) {
                    user = new User();
                    user.setFirstname(source.getString("firstname"));
                    user.setLastname(source.getString("lastname"));
                    user.setUsername(source.getString("username"));
                    user.setUser_preference(source.getJSONObject("user_preference"));

                }
                else {
                    user = null;
                }

            } catch (IOException e) {


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }
        @Override
        protected void onPostExecute(User result) {
            if(result != null) {
                Intent it = new Intent(LoginActivity.this, Dashboard.class);
                it.putExtra("User", result);
                startActivity(it);
                finish();
            }

        }
    }
}