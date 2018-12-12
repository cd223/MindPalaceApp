package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void goToIndoorLocation(View view) {
        Intent intent = new Intent(this, IndoorLocation.class);
        startActivity(intent);
    }

    public void goToCreatePalace(View view) {
        Intent intent = new Intent(this, CreatePalace.class);
        startActivity(intent);
    }

    public void goToCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class);
        startActivity(intent);
    }

    public void goToCreateUser(View view) {
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }
}
