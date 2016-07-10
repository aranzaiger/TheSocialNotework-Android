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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private Button registerButton;
    private Button testBtn;
    private RegisterActivity self;
    protected RelativeLayout layout;
    private final String TAG = "[TSN/RegisterActivity]";
    private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private final String REG_PATH = "/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.mUsernameView = (EditText) findViewById(R.id.ra_username); // holds the username
        this.mPasswordView = (EditText) findViewById(R.id.ra_password); // holds the password
        this.mEmailView = (EditText) findViewById(R.id.ra_email); // holds the email

        this.self = this;
        this.layout = (RelativeLayout) findViewById(R.id.ra_layout);

        // Remove Auto Focus from the Text Fields
        this.layout.setFocusable(true);
        this.layout.setFocusableInTouchMode(true);

        // Buttons And Listeners
        this.testBtn = (Button) findViewById(R.id.btn_cancel);
        this.testBtn.setOnClickListener(this);
        this.registerButton = (Button) findViewById(R.id.ra_register_button);
        this.registerButton.setOnClickListener(this);
    }

    /**
     * username validation
     * @param username
     * @return
     */
    private boolean isUsernameValid(String username) {
        return !TextUtils.isEmpty(username) && username.length() > 0;
    }

    /**
     * password validation
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 3;
    }

    /**
     * email validation
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email))
            return false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * private method that validates all params
     * @param username
     * @param password
     * @param email
     * @return
     */
    private boolean isParamsValid(String username, String password, String email) {
        return (isUsernameValid(username) && isPasswordValid(password) && isEmailValid(email));
    }

    /**
     * attempt registering
     */
    private void attemptRegister() {
        Utils.showLoadingDialog(this, "Registering", "Please Wait...");
        if (isParamsValid(mUsernameView.getText().toString(), mPasswordView.getText().toString(), mEmailView.getText().toString())) { // params are valid
            String username = mUsernameView.getText().toString();
            String password = mPasswordView.getText().toString();
            String email = mEmailView.getText().toString();
            // http request register
            JSONObject tempJson = new JSONObject();
            try { // creating a user json to send the server
                tempJson.put("username", username);
                tempJson.put("password", password);
                tempJson.put("email", email);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            VolleyUtilSingleton.getInstance(RegisterActivity.this).post(BASE_URL + REG_PATH, tempJson, onRegisterSuccess, onRegisterError); // register request to server
        } else { // invalid params
            Utils.dismissLoadingDialog();
            Toast.makeText(this, "Make Sure tou have entered a valid email. password at least 4 chars", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * listener to success response on register from server
     */
    Response.Listener<JSONObject> onRegisterSuccess = new Response.Listener<JSONObject>() {
        /**
         * on response register from server
         * @param response
         */
        @Override
        public void onResponse(JSONObject response) {
            Utils.dismissLoadingDialog();
            try {
                if (response.getString("message").equals("created")) { // user created
                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                    Toast.makeText(self, "You are now a social notework member - You May Login...", Toast.LENGTH_LONG).show();
                    startActivity(loginActivity);
                } else {
                    Toast.makeText(self, "Username is already taken. maybe: " + mUsernameView.getText().toString() + "_666 ? :)", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) { // error on register user request
                Log.e(TAG, "onRegisterSuccess:" + e.getMessage());

            }
        }
    };


    Response.ErrorListener onRegisterError = new Response.ErrorListener() {
        /**
         * listener to error response on register from server
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            Utils.dismissLoadingDialog();
            Toast.makeText(self, "Username is already taken. maybe: " + mUsernameView.getText().toString() + "_666 ? :)", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * onclick methods to redirect to register and to login
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ra_register_button:
                attemptRegister();
                break;
            case R.id.btn_cancel:
                returnToLogin();
                break;
        }
    }

    /**
     * redirect to login
     */
    private void returnToLogin() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
