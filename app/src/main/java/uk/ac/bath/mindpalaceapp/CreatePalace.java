package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreatePalace extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newpalace";
    private static final String TAG = CreatePalace.class.getName();
    private String username;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_create_palace);
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");
    }

    public void createNewPalace(View view) {
        final EditText mPalaceTitle = findViewById(R.id.palaceCreationTitle);
        final EditText mPalaceDescription = findViewById(R.id.palaceDescription);
        final View curView = view;
        if(mPalaceTitle.getText() == null || mPalaceDescription.getText() == null
           || mPalaceTitle.getText().toString().isEmpty() || mPalaceDescription.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please enter a title and description before clicking 'Create Palace'.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Map<String, Object> json = new HashMap<>();
        json.put("user_username", username);
        json.put("palace_title", mPalaceTitle.getText().toString());
        json.put("palace_description", mPalaceDescription.getText().toString());
        Log.d(TAG, json.toString());

        JSONObject jsonObject = new JSONObject(json);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Palace '" + mPalaceTitle.getText().toString() + "' created.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(curView.getContext(), MainMenu.class);
                        intent.putExtra("user_username", username);
                        intent.putExtra("user_name", name);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.info_screen) {
            Intent intent = new Intent(getApplicationContext(), CreatePalaceInfo.class);
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
