package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by sagi on 7/7/16. - A Basic User Class
 *   "user": {
 "id": 5733311175458816,
 "username": "s",
 "password": "1234",
 "email": "sagi@dayan.com",
 "creation_time": 1467814417313,
 "avatar": "http://www.aljazeera.com/mritems/images/site/DefaultAvatar.jpg",
 "liked_notes_id": []
 }
 */
public class User {
  public static final int INDEX_ID = 0, INDEX_USERNAME = 1, INDEX_PASSWORD = 2, INDEX_EMAIL = 3, INDEX_AVATAR = 4, INDEX_LIKES_NOTES = 5;
  public static final String TAG = "[TSN/User]", ATTARS_DELIMETER="||" , LIKED_NOTES_DELIMETER="|";
  protected String id, password, email, avatar, username;
  protected Vector<String> liked_notes;
  protected int number_of_notes;
  protected User self;

  public User(String serializedUserData){
    self = this;
    liked_notes = new Vector<>();
    number_of_notes = 0;
    String[] array = serializedUserData.split("\\|\\|");
    for (int i = 0 ; i < array.length ; i ++){
      switch (i){
        case INDEX_ID:
          this.id = array[i];
          break;
        case INDEX_AVATAR:
          this.avatar = array[i];
          break;
        case INDEX_EMAIL:
          this.email = array[i];
          break;
        case INDEX_LIKES_NOTES:
          createArrayNotes(array[i]);
          break;
        case INDEX_PASSWORD:
          this.password = array[i];
          break;
        case INDEX_USERNAME:
          this.username = array[i];
          break;
        default:
          Log.w(TAG, "User: Got An Unowned value: " + array[i]);
          break;
      }
    }
    Log.d(TAG, "User: Constructor Created:\n"+this.toString());
  }

  public int getNumber_of_notes() {
    return number_of_notes;
  }

  public void setNumber_of_notes(int number_of_notes) {
    this.number_of_notes = number_of_notes;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getUsername() {
    return username;
  }


  public Vector<String> getLiked_notes() {
    return liked_notes;
  }

  private void createArrayNotes(String s) {
    Log.d(TAG, "createArrayNotes:" + s);
    String[] noteIds = s.split("\\|");
    for (int i = 0; i < noteIds.length; i++) {
      Log.d(TAG, "createArrayNotes: Note ID " + i + ": " + noteIds[i]);
      liked_notes.add(noteIds[i]);
    }
  }

  public String Serialise(){
    return id + ATTARS_DELIMETER + username + ATTARS_DELIMETER + password + ATTARS_DELIMETER + email + ATTARS_DELIMETER +avatar + ATTARS_DELIMETER + serialiseNoteList();
  }

  private String serialiseNoteList() {
    String result = "";
    for (int i = 0; i < liked_notes.size(); i++) {
      result += liked_notes.get(i);
      if(i != liked_notes.size() - 1){
        result+=User.LIKED_NOTES_DELIMETER;
      }
    }
    return result;
  }

  public String toString(){
    return "Id: "+id+" UserName: " + username +" Password: " +password +" email: " + email+ " Avatar: " +avatar+" Liked Notes: "+liked_notes.toString();
  }

  public void updateUser(final MainActivity activity){
    VolleyUtilSingleton.getInstance(activity).post(Utils.BASE_URL + "/user/upsert", this.toJSON(), new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          if(response.get("status").equals("OK")){
            activity.updateNavAvatar();
            Utils.updateUserSharedPref(self.Serialise());
            activity.updateNavAvatar();
          }
        } catch (JSONException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, Utils.genericErrorListener);
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    JSONArray liked = new JSONArray();
    for (int i = 0; i < liked_notes.size(); i++) {
      liked.put(liked_notes.get(i));
    }
    try {
      json.put("id", this.id);
      json.put("password", this.password);
      json.put("username", this.username);
      json.put("email", this.email);
      json.put("avatar", this.avatar);
      json.put("liked_notes_id", liked);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }


}
