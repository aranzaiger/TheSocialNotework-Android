package com.android_app.matan.ara.sagi.thesocialnotework;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by JERLocal on 7/1/2016.
 * A Note Class - Represents a note Object
 */
public class Note implements Parcelable {

  protected int likes;
  protected ArrayList<String> tags;
  protected float lat, lon;
  protected String id, address, title, body, timestamp, avatar, ownerId;
  protected boolean isPublic;


  public Note(String id, float lat, float lon, String address, String title, String body, String timestamp, boolean isPublic, int likes, String avatar, String ownerId, ArrayList<String> tags) {
    this.id = id;
    this.lat = lat;
    this.lon = lon;
    this.address = address;
    this.title = title;
    this.body = body;
    this.timestamp = timestamp;
    this.isPublic = isPublic;
    this.likes = likes;
    this.tags = tags;
    this.avatar = avatar;
    this.ownerId = ownerId;
  }


  protected Note(Parcel in) {
    likes = in.readInt();
    tags = in.createStringArrayList();
    lat = in.readFloat();
    lon = in.readFloat();
    id = in.readString();
    address = in.readString();
    title = in.readString();
    body = in.readString();
    timestamp = in.readString();
    avatar = in.readString();
    ownerId = in.readString();
    isPublic = in.readByte() != 0;
  }

  public static final Creator<Note> CREATOR = new Creator<Note>() {
    @Override
    public Note createFromParcel(Parcel in) {
      return new Note(in);
    }

    @Override
    public Note[] newArray(int size) {
      return new Note[size];
    }
  };


  @Override
  public String toString() {
    return "Note{" +
      "id=" + id +
      ", lat=" + lat +
      ", lon=" + lon +

      ", address='" + address + '\'' +
      ", title='" + title + '\'' +
      ", body='" + body + '\'' +
      ", timestamp=" + timestamp +
      ", isPublic=" + isPublic +
      ", avatar=" + avatar +
      ", ownerId=" + ownerId +
      '}';
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public float getLat() {
    return lat;
  }

  public void setLat(float lat) {
    this.lat = lat;
  }

  public float getLon() {
    return lon;
  }

  public void setLon(float lon) {
    this.lon = lon;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean aPublic) {
    isPublic = aPublic;
  }

  public String getLocationAndTime() {
    return getTimestamp() + " at " + getAddress();
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public ArrayList<String> getTags() {
    return tags;
  }

  public void setTags(ArrayList<String> tags) {
    this.tags = tags;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(likes);
    dest.writeList(tags);
    dest.writeFloat(lat);
    dest.writeFloat(lon);
    dest.writeString(id);
    dest.writeString(address);
    dest.writeString(title);
    dest.writeString(body);
    dest.writeString(timestamp);
    dest.writeString(avatar);
    dest.writeString(ownerId);
    dest.writeByte((byte) (isPublic ? 1 : 0));
  }

  /**
   * Get a Simplified Readable Time (Creation of the note)
   *
   * @return String
   */
  public String getTime() {
    Date d = new Date(timestamp);
    DateFormat format = new SimpleDateFormat("HH:mm");
    return format.format(d);
  }

  /**
   * Get a Simplified Readable Date (Creation of the note)
   *
   * @return String
   */
  public String getDate() {
    Date d = new Date(timestamp);
    DateFormat format = new SimpleDateFormat("dd MMM yyy");
    return format.format(d);
  }

}
