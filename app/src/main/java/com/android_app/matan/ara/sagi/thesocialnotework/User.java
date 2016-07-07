package com.android_app.matan.ara.sagi.thesocialnotework;

import android.util.Log;

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

  public User(String serializedUserData){
    liked_notes = new Vector<>();
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
    return email;
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

  public void setUsername(String username) {
    this.username = username;
  }

  public Vector<String> getLiked_notes() {
    return liked_notes;
  }

  public void setLiked_notes(Vector<String> liked_notes) {
    this.liked_notes = liked_notes;
  }

  private void createArrayNotes(String s) {
    Log.d(TAG, "createArrayNotes:" + s);
    String[] noteIds = s.split("\\|");
    for (int i = 0; i < noteIds.length; i++) {
      Log.d(TAG, "createArrayNotes: Note ID " + i + ": " + noteIds[i]);
      liked_notes.add(noteIds[i]);
    }
    Log.d(TAG, "createArrayNotes: =================: == Done With Note IDS");
  }

  public String Serialise(){
    return id + "||" + username + "||" + password + "||" + email + "||" + serialiseNoteList();
  }

  private String serialiseNoteList() {
    String result = "";
    for (int i = 0; i < liked_notes.size(); i++) {
      result += liked_notes.get(i);
      if(i != liked_notes.size() - 1){
        result+=";";
      }
    }
    return result;
  }

  public String toString(){
    return "Id: "+id+" UserName: " + username +" Password: " +password +" email: " + email+ " Liked Notes: "+liked_notes.toString();
  }



}