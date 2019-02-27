package uk.ac.bath.mindpalaceapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ViewNoteInfo extends AppCompatActivity {

    private static final String TAG = ViewNoteInfo.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note_info);
    }
}
