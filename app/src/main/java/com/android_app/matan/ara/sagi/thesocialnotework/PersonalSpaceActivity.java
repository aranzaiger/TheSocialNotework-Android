package com.android_app.matan.ara.sagi.thesocialnotework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PersonalSpaceActivity extends AppCompatActivity {

    protected ListView noteList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_space);
        Note n1 = new Note(1, 100, 100, "location1", "My 1st Title", "ohh i'm so sexy1",  System.currentTimeMillis()/1000, true);
        Note n2 = new Note(2, 200, 200, "location2", "My 2st Title", "ohh i'm so sexy2",  System.currentTimeMillis()/1000, true);
        Note n3 = new Note(3, 300, 300, "hell", "My 3st Title", "ohh i'm so sexy3",  System.currentTimeMillis()/1000, true);
        Note n4 = new Note(4, 400, 400, "hell2", "My 4st Title", "ohh i'm so sexy4",  System.currentTimeMillis()/1000, true);
        List<Note> listOfNotes = new ArrayList<>();
        listOfNotes.add(n1);
        listOfNotes.add(n2);
        listOfNotes.add(n3);
        listOfNotes.add(n4);
        ListAdapter la = new ListAdapter(this, listOfNotes);
        this.noteList = (ListView)findViewById(R.id.ps_list_listview);
        noteList.setAdapter(la);


    }
}
