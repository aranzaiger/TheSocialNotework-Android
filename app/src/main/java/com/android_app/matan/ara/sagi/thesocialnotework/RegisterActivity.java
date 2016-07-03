package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

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

        Log.d(TAG, "oncreate...");
        testBtn = (Button) findViewById(R.id.test_button);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "NOW??.......");

            }
        });

        registerButton = (Button) findViewById(R.id.ra_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "here.......");
                attemptRegister();
            }
        });

    }

    private boolean isUsernameValid(String username) {
        return username.length() > 0;
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }
    private boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        Log.d(TAG, "isValid: " + isValid);
        return isValid;
    }

    private boolean isParamsValid(String username, String password, String email) {
        Log.d(TAG, "user: " + username);
        Log.d(TAG, "pwd: " + password);
        Log.d(TAG, "email: " + email);

        if(TextUtils.isEmpty(username) || !isUsernameValid(username) || TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            return false;
        } else if(TextUtils.isEmpty(email) || !isEmailValid(email)) {
            return false;
        } else {
            return true;
        }
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

            VolleyUtilSingleton.getInstance(RegisterActivity.this).post(BASE_URL + REG_PATH, tempJson, onRegisterSuccess, onRegisterError);
        } else {
//            showProgress(false);
            Log.d(TAG, "Invalid params - make sure username exist, password is 4 characters or more & email is valid");
        }
    }

    Response.Listener<JSONObject> onRegisterSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if(!response.isNull("user")) {
                    Log.e(TAG, "onLoginSuccess => user exist"); // TODO: REMOVE console
                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
//                    Bundle loginUserBundle = new Bundle();
//                    loginUserBundle.putString("user_id", response.getJSONObject("user").getString("id"));
//                    personalSpaceActivity.putExtras(loginUserBundle);
                    startActivity(loginActivity);
                } else {
//                    showProgress(false);
                    Log.d(TAG, "Cannot create user, " + response.get("user"));
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
            Toast.makeText(self , "Username, Password  or Email are Incorrect", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onRegisterError: msg: " + error.getMessage());
        }
    };
}
