package com.spartan.karanbir.restorecommender;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.List;

/**
 * Created by karanbir on 4/27/16.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {
    public static final String EMAIL_KEY = "email";
    public static final String FIRSTNAME_KEY = "firstName";
    public static final String LASTNAME_KEY = "lastName";
    public static final String PASSWORD_KEY = "password";
    public static final String BUNDLE_KEY = "UserData";

    private Validator validator;
    private Button mNextButton;
    @NotEmpty
    private EditText mFirstName;
    @NotEmpty
    private EditText mLastName;
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC, message = "Must be 6 chars and Alphanumeric")
    private EditText mPassword;
    @NotEmpty
    @Email
    private EditText mEmail;
    private HashMap<String,String> hashMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        validator = new Validator(this);
        validator.setValidationListener(this);
        hashMap = new HashMap<>();
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_button:
                validator.validate();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        Intent i = new Intent(this, SubCategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EMAIL_KEY, mEmail.getText().toString());
        bundle.putString(FIRSTNAME_KEY, mFirstName.getText().toString());
        bundle.putString(LASTNAME_KEY, mLastName.getText().toString());
        bundle.putString(PASSWORD_KEY, mPassword.getText().toString());
        i.putExtra(BUNDLE_KEY,bundle);
        startActivity(i);
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
}
