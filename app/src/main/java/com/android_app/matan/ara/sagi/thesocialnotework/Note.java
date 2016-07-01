package com.android_app.matan.ara.sagi.thesocialnotework;

/**
 * Created by JERLocal on 7/1/2016.
 */
public class Note {

    protected int id;
    protected float lat, lon;
    protected String address, title, body;
    protected long timestamp;
    protected boolean isPublic;

    public Note(int id, float lat, float lon, String address, String title, String body, long timestamp, boolean isPublic) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.isPublic = isPublic;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
