package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private Button registerButton;
    private Button testBtn;
    private RegisterActivity self;

    protected RelativeLayout layout;
    private final String TAG = "Register Activity";

    private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private final String REG_PATH = "/register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView = (EditText) findViewById(R.id.ra_username);
        mPasswordView = (EditText) findViewById(R.id.ra_password);
        mEmailView = (EditText) findViewById(R.id.ra_email);

        this.self = this;
        this.layout = (RelativeLayout) findViewById(R.id.ra_layout);

        // Remove Auto Focus from the Text Fields
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);

        testBtn = (Button) findViewById(R.id.btn_cancel);
        testBtn.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.ra_register_button);
        registerButton.setOnClickListener(this);

    }

    private boolean isUsernameValid(String username) {
        return !TextUtils.isEmpty(username) && username.length() > 0;
    }
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 3;
    }
    private boolean isEmailValid(String email) {
        if(TextUtils.isEmpty(email))
                return false;
//        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            isValid = true;
//        }
//        Log.d(TAG, "isValid: " + isValid);
        return matcher.matches();
    }

    private boolean isParamsValid(String username, String password, String email) {
        return (isUsernameValid(username) && isPasswordValid(password) && isEmailValid(email));
    }

    private void attemptRegister() {
//        showProgress(true);
        Log.d(TAG, "in attemptRegister: Registering..");
        if (isParamsValid(mUsernameView.getText().toString(), mPasswordView.getText().toString(), mEmailView.getText().toString())) {

            Log.d(TAG, "params are valid");

            String username = mUsernameView.getText().toString();
            String password = mPasswordView.getText().toString();
            String email = mEmailView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // http request register
            JSONObject tempJson = new JSONObject();
            try {
                tempJson.put("username", username);
                tempJson.put("password", password);
                tempJson.put("email", email);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            Log.d(TAG,"JSON: "+tempJson.toString());

            VolleyUtilSingleton.getInstance(RegisterActivity.this).post(BASE_URL + REG_PATH, tempJson, onRegisterSuccess, onRegisterError);
        } else {
//            showProgress(false);
            Log.d(TAG, "Invalid params - make sure username exist, password is 4 characters or more & email is valid");
            Toast.makeText(this, "Make Sure tou have entered a valid email. password at least 4 chars", Toast.LENGTH_LONG).show();
        }
    }

    Response.Listener<JSONObject> onRegisterSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
           Log.d(TAG,"reposne: "+ response.toString());
            try {
                if(response.getString("message").equals("created")) {

                    Log.d(TAG, "onRegisterSuccess => user created"); // TODO: REMOVE console
                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);

                    Toast.makeText(self, "You are now a social notework member - You May Login...", Toast.LENGTH_LONG).show();
                    startActivity(loginActivity);
                } else {
//                    showProgress(false);
                    Toast.makeText(self , "Username is already taken. maybe: " + mUsernameView.getText().toString()+"_666 ? :)", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Cannot create user, " + response.getString("message"));
                }
            }catch (Exception e) {
                Log.e(TAG, "onRegisterSuccess:" + e.getMessage());

            }
        }
    };

    Response.ErrorListener onRegisterError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
//            showProgress(false);
            Toast.makeText(self , "Username is already taken. maybe: " + mUsernameView.getText().toString()+"_666 ? :)", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onRegisterError: msg: " + error.getMessage());
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ra_register_button:
                attemptRegister();
                break;
            case R.id.btn_cancel:

                returnToLogin();
                break;
        }
    }

    private void returnToLogin(){
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
