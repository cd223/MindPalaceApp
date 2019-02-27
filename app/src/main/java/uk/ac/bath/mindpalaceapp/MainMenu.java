package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private String username;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main_menu);
        TextView welcomeMsg = findViewById(R.id.welcomeMsg);
        name = getIntent().getStringExtra("user_name").split(" ")[0];
        username = getIntent().getStringExtra("user_username");
        welcomeMsg.setText("Hello, " + name);
    }

    public void signOut(View view) {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void goToCreateMenu(View view) {
        Intent intent = new Intent(this, CreateMenu.class);
        intent.putExtra("user_username", username);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    public void goToTrainMenu(View view) {
        Intent intent = new Intent(this, TrainMenu.class);
        intent.putExtra("user_username", username);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    public void goToTrackMenu(View view) {
        Intent intent = new Intent(this, TrackMenu.class);
        intent.putExtra("user_username", username);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsMenu.class);
        intent.putExtra("user_username", username);
        startActivity(intent);
    }
}
