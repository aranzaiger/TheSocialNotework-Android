package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PersonalSpaceActivity extends AppCompatActivity {

    protected ListView noteList;
    protected Button addBtn;
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

        addBtn = (Button) findViewById(R.id.ps_new_note_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PersonalSpaceActivity.this);

                dialog.setContentView(R.layout.note_view_full);
                dialog.setTitle("New Note");
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;


//                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
//                Button btnSave          = (Button) dialog.findViewById(R.id.save);
//                Button btnCancel        = (Button) dialog.findViewById(R.id.cancel);
                dialog.setCancelable(false);
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });


    }
}
