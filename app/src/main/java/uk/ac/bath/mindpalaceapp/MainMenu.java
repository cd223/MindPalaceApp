package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        TextView welcomeMsg = findViewById(R.id.welcomeMsg);
        welcomeMsg.setText("Hello, " + getIntent().getStringExtra("user_name").split(" ")[0]);
    }

    public void signOut(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void goToCreateMenu(View view) {
        Intent intent = new Intent(this, CreateMenu.class);
        startActivity(intent);
    }

    public void goToTrainMenu(View view) {
        Intent intent = new Intent(this, TrainMenu.class);
        startActivity(intent);
    }

    public void goToTrackMenu(View view) {
        Intent intent = new Intent(this, TrackMenu.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsMenu.class);
        startActivity(intent);
    }
}
