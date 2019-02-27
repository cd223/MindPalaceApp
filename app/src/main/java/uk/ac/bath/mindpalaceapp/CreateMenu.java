package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CreateMenu extends AppCompatActivity {

    public String username;
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_create_menu);
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");
    }

    public void goToCreatePalace(View view) {
        Intent intent = new Intent(this, CreatePalace.class);
        intent.putExtra("user_username", username);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    public void goToCreateNote(View view) {
        Intent intent = new Intent(this, CreateNote.class);
        intent.putExtra("user_username", username);
        intent.putExtra("user_name", name);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.info_screen) {
            Intent intent = new Intent(getApplicationContext(), CreateMenuInfo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
}
