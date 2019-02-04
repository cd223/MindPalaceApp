package uk.ac.bath.mindpalaceapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class NoteDetails extends AppCompatActivity {

    private String note_image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_note_details);

        note_image_url = getIntent().getStringExtra("note_image_url");

        ImageView noteImageView = findViewById(R.id.checkedImage);
        ImageLoader.getLoader(getApplicationContext()).setLoggingEnabled(true);
        ImageLoader.getLoader(getApplicationContext())
                .load(note_image_url)
                .fit()
                .centerCrop()
                .into(noteImageView);

        noteImageView.setClickable(true);

        noteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
