package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {


  // UI references.
  private EditText mUsernameView;
  private EditText mPasswordView;
  private TextView mRegisterButton;
  private Button mLoginButton;
  private final String TAG = "[TSN/LoginActivity]";
  private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
  private final String LOGIN_PATH = "/login";
  private LoginActivity self; //this
  protected LinearLayout layout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    this.self = this;
    this.layout = (LinearLayout) findViewById(R.id.layout);
    this.mUsernameView = (EditText) findViewById(R.id.al_username);
    this.mUsernameView.addTextChangedListener(this);
    this.mPasswordView = (EditText) findViewById(R.id.al_password);
    this.mPasswordView.addTextChangedListener(this);
    removeFocuse();
    this.mRegisterButton = (TextView) findViewById(R.id.al_register_button);
    this.mRegisterButton.setOnClickListener(this);

    this.mLoginButton = (Button) findViewById(R.id.al_login_button);
    this.mLoginButton.setOnClickListener(this);
    this.mLoginButton.setEnabled(false);

  }

  private void removeFocuse() {
    // Remove Auto Focus from the Text Fields
    mUsernameView.clearFocus();
    mPasswordView.clearFocus();
    layout.setFocusable(true);
    layout.setFocusableInTouchMode(true);

  }


  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */

  private boolean isParamsValid(String username, String password) {
    Log.d(TAG, "user: " + username);
    Log.d(TAG, "pwd: " + password);

        if(TextUtils.isEmpty(username) || !isUsernameValid(username) || TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            return false;
        } else{
            return true;
        }
    }
    private void attemptLogin() {
        Utils.showLoadingDialog(this, "Connecting", "Authenticating data");
        mPasswordView.setError(null);
        if (isParamsValid(mUsernameView.getText().toString(), mPasswordView.getText().toString())) {

      String username = mUsernameView.getText().toString();
      String password = mPasswordView.getText().toString();

            boolean cancel = false;
            // http request register
            JSONObject tempJson = new JSONObject();
            try {
                tempJson.put("username", username);
                tempJson.put("password", password);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            VolleyUtilSingleton.getInstance(LoginActivity.this).post(Utils.BASE_URL + LOGIN_PATH, tempJson, onLoginSuccess, onLoginError);
        } else {
            Utils.dismissLoadingDialog();
            Log.d(TAG, "Invalid params - make sure username exist & password is 4 characters or more");
        }

  }

  Response.Listener<JSONObject> onLoginSuccess = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
      try {
        if (!response.isNull("user")) {
          Log.e(TAG, "onLoginSuccess => user exist"); // TODO: REMOVE console
          SharedPreferences sharedPref = self.getSharedPreferences(MainActivity.LOCAL_DATA_TSN, Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = sharedPref.edit();
          String id, password, email, avatar, username, likedNotes = "";

          JSONArray likedNotes_JSON;
          id = response.getJSONObject("user").getString("id");
          password = response.getJSONObject("user").getString("password");
          username = response.getJSONObject("user").getString("username");
          avatar = response.getJSONObject("user").getString("avatar");
          email = response.getJSONObject("user").getString("email");
          likedNotes_JSON = response.getJSONObject("user").getJSONArray("liked_notes_id");
          for (int i = 0; i < likedNotes_JSON.length(); i++) {
            likedNotes += likedNotes_JSON.get(i);
            if (i != likedNotes_JSON.length() - 1) {
              likedNotes += User.LIKED_NOTES_DELIMETER;
            }
          }
          editor.putString("UserData", id+User.ATTARS_DELIMETER+username+User.ATTARS_DELIMETER+password+User.ATTARS_DELIMETER+email+User.ATTARS_DELIMETER+avatar+User.ATTARS_DELIMETER+likedNotes);
          editor.commit();
          Intent personalSpaceActivity = new Intent(LoginActivity.this, MainActivity.class);
          Bundle loginUserBundle = new Bundle();
          loginUserBundle.putString("UserData", id+User.ATTARS_DELIMETER+username+User.ATTARS_DELIMETER+password+User.ATTARS_DELIMETER+email+User.ATTARS_DELIMETER+avatar+User.ATTARS_DELIMETER+likedNotes);
          personalSpaceActivity.putExtras(loginUserBundle);
            Utils.dismissLoadingDialog();
          startActivity(personalSpaceActivity);
        } else {
            Utils.dismissLoadingDialog();
          Toast.makeText(self, "Username or Password are incorrect", Toast.LENGTH_LONG).show();
          self.mUsernameView.getText().clear();
          self.mPasswordView.getText().clear();
          self.removeFocuse();
          ((TextView) findViewById(R.id.textView2)).setVisibility(View.INVISIBLE);
          Log.d(TAG, "No such user, " + response.get("user"));

        }
      } catch (Exception e) {
        Log.e(TAG, "onLoginSuccess:" + e.getMessage());
      }
    }
  };

    Response.ErrorListener onLoginError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Utils.dismissLoadingDialog();
            Toast.makeText(self , "Username Or Password Incorrect", Toast.LENGTH_LONG).show();
            //Clean texts
            self.mUsernameView.getText().clear();
            self.mPasswordView.getText().clear();
            self.removeFocuse();
            Log.d(TAG, "onErrorResponse: setting text to ''");
            Log.d(TAG, "onLoginError: msg: " + error.getMessage());
        }
    };


  private boolean isUsernameValid(String username) {
    return username.length() > 0;
  }

  private boolean isPasswordValid(String password) {
    return password.length() > 3;
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.al_login_button:
        //Do Login
        Log.d(TAG, "Login.......");
        attemptLogin();
        break;
      case R.id.al_register_button:
        //Do Register
        Log.d(TAG, "going to Register...page");
        Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerActivity);
        break;
    }
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

  }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    if (isParamsValid(mUsernameView.getText().toString(), mPasswordView.getText().toString())) {
      ((TextView) findViewById(R.id.textView2)).setVisibility(View.INVISIBLE);
      mLoginButton.setEnabled(true);
    } else {
      ((TextView) findViewById(R.id.textView2)).setVisibility(View.VISIBLE);
      mLoginButton.setEnabled(false);
    }
  }

  @Override
  public void afterTextChanged(Editable editable) {

  }
}

