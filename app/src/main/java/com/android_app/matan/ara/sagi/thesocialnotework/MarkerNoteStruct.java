package com.android_app.matan.ara.sagi.thesocialnotework;

import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by JERLocal on 7/8/2016.
 * This class will hold information about a Mark on the Map.
 */
public class MarkerNoteStruct {
    private Note note;
    private MarkerOptions markerOptions;

    public  MarkerNoteStruct(Note note, MarkerOptions markerOptions){
        this.note = note;
        this.markerOptions = markerOptions;
    }

    public Note getNote() {
        return note;
    }

    public MarkerOptions getMarker() {
        return markerOptions;
    }
}
