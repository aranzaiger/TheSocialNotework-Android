package com.android_app.matan.ara.sagi.thesocialnotework;

import java.util.ArrayList;

/**
 * Created by JERLocal on 7/1/2016.
 */
public class Note {

    protected int likes;
    protected ArrayList<String> tags;
    protected float lat, lon;
    protected String id, address, title, body, timestamp;
    protected boolean isPublic;



    public Note(String id, float lat, float lon, String address, String title, String body, String timestamp, boolean isPublic, int likes, ArrayList<String> tags) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;

        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.isPublic = isPublic;
        this.likes = likes;
        this.tags =  tags;
    }

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
        return getTimestamp()+" at "+ getAddress();
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

//    public void save(SQLiteOpenHelper dbHelper, Context context){
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(appDB.LocationsEntry.ADDRESS, this.address);
//
//        String where = appDB.LocationsEntry._ID + " =?";
//        String[] whereArgs = {Integer.toString(this.id)};
//
//        db.update(appDB.LocationsEntry.TABLE_NAME, values, where, whereArgs);
//
//        db.close();
//
//        Toast.makeText(context, "Address saved!", Toast.LENGTH_LONG).show();
//    }
//
//    public static Cursor getAll(DBHelper dbHelper) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String[] selectionArgs = {};
//
//        return db.rawQuery("SELECT * FROM "+appDB.LocationsEntry.TABLE_NAME, selectionArgs);
//    }

}
