package com.android_app.matan.ara.sagi.thesocialnotework;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by JERLocal on 7/8/2016.
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
